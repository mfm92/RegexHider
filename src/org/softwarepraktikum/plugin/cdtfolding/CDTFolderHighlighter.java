package org.softwarepraktikum.plugin.cdtfolding;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
import org.softwarepraktikum.plugin.CDTFolderPlugin;

public class CDTFolderHighlighter {

	enum ActionChoice {
		Folding, Highlighting, Error
	}

	ProjectionAnnotationModel projectionAnnotationModel;

	boolean debug = false;

	/*
	 * This is the method called from outside if folding or highlighting is
	 * about to happen...
	 */
	public void apply (ITextEditor editor, ProjectionViewer viewer) {
		System.out.println("CDTFolder.collapse()");

		if (viewer.getProjectionAnnotationModel() != null) {
			// save projection annotation model of the viewer
			this.projectionAnnotationModel = viewer.getProjectionAnnotationModel();

			/*
			 * Here we remove all annotations that had been added previously. It
			 * is needed to do that to get rid of highlighted parts that do not
			 * correspond to a match of the new regex.
			 */
			editor.doRevertToSaved();
			expand();

			// Grab preference store, we use this to get access to user-defined
			// configurations
			IPreferenceStore store = CDTFolderPlugin.getDefault().getPreferenceStore();

			/*
			 * Get the user specified regular expression. This is an
			 * or-concatenation of one or more regular expressions.
			 */
			String regex = store.getString(CDTFoldingConstants.CHECKED_STRING_INPUT);

			// Do we want to fold lines or highlight matches?
			String choiceID = store.getString(CDTFoldingConstants.COMBO_CHOICE);

			ActionChoice actionChoice = choiceID.equals(CDTFoldingConstants.COMBO_CHOICE_FOLD) ? ActionChoice.Folding
					: choiceID.equals(CDTFoldingConstants.COMBO_CHOICE_HIGHLIGHT) ? ActionChoice.Highlighting
							: ActionChoice.Error;

			if (debug) {
				System.out.println("Current regex is: " + regex);
				System.out.println("ActionChoice is: " + actionChoice);
			}

			// Read the content of the currently open file in the IDE.
			String content = getCurrentEditorContent(editor);

			// Call other methods depending on whether the user wants to fold or
			// highlight...
			switch (actionChoice) {
				case Folding:
					fold(regex, content);
					break;
				case Highlighting:
					highlight(regex, content, viewer);
					break;
				case Error:
					showError();
					break;
			}
		} else {
			System.out.println("ProjectionAnnotationModel is null!");
		}
	}

	/*
	 * Gets a regular expression and the content of the file that is currently
	 * open in the IDE as input. Based on that, projection annotations are added
	 * to the projection annotation model of the viewer of the editor to create
	 * the folding structure.
	 */
	private void fold (String regex, String content) {
		System.out.println("CDTFolder.fold()");

		// What is the first index in every line?
		Map<Integer, Integer> newLineMap = preProcess(content);

		// Get the index ranges of each match...
		Set<Map.Entry<Integer, Integer>> mlSet = getMatchingLines(regex, content, newLineMap).entrySet();

		// Iterate over the computed folded sections...
		for (FoldingSection foldedSection : getFoldedSections(newLineMap, mlSet)) {
			ProjectionAnnotation pa = new ProjectionAnnotation();

			// In what line does the match start?
			int startLine = foldedSection.getStartLine();

			// In what does it end?
			int endLine = foldedSection.getEndLine();

			/*
			 * What is the index of the first character in the start line? Note
			 * that we pass startLine-1 rather than startLine as argument to the
			 * get method of newLineMap.
			 */
			int startIdx = newLineMap.get(startLine - 1);

			/*
			 * Same thing for the index of the first character in the last line
			 * of the current match.
			 */
			int endIdx = newLineMap.get(endLine >= newLineMap.size() ? endLine - 1 : endLine) - 1;

			// Add annotation...
			projectionAnnotationModel.addAnnotation(pa, new Position(startIdx, endIdx - startIdx));

			// Fold it...
			projectionAnnotationModel.collapse(pa);
			pa.markCollapsed();
		}
	}

	/*
	 * This method takes care of highlighting all matches of a regular
	 * expression within the view of the content of the currently opened file of
	 * an Eclipse instance. We'll pass the user-specified regular expression,
	 * the content of the current file and the projection viewer as input to
	 * this method. Based on that it will compute the matches of the regex
	 * within the content and add style ranges (user-specified colors) to the
	 * view.
	 */
	private void highlight (String regex, String content, ProjectionViewer viewer) {
		System.out.println("CDTFolder.highlight()");

		// What is the index of the first character in each line?
		Map<Integer, Integer> newLineMap = preProcess(content);

		// Empty list of style ranges...
		ArrayList<StyleRange> styleRanges = new ArrayList<>();

		/*
		 * Useful reference to the text widget of the viewer. Style ranges will
		 * be added to this widget that make it possible for the user to see the
		 * highlighted matches...
		 */
		StyledText styledText = viewer.getTextWidget();

		/*
		 * Update the content of the widget if necessary. This has to be done
		 * for example if the user wants to highlight matches of a regular
		 * expression while sections of the content of the open file are still
		 * folded.
		 */
		if (viewer.getTextWidget().getCharCount() != content.length()) {
			viewer.getTextWidget().setText(content);
		}

		// Reference to preference store...
		IPreferenceStore store = CDTFolderPlugin.getDefault().getPreferenceStore();

		// Restore the user-specified background color of all matches...
		RGB bgRGB = CDTUtilities.restoreRGB(store.getString(CDTFoldingConstants.COLOR_PICKED_BG));

		// Same thing for the text/foreground color...
		RGB fgRGB = CDTUtilities.restoreRGB(store.getString(CDTFoldingConstants.COLOR_PICKED_FG));

		// Compute all matches...
		Set<Map.Entry<Integer, Integer>> mlSet = getMatchingLines(regex, content, newLineMap).entrySet();

		if (debug) {
			System.out.println("Content: " + content);
			System.out.println("Highlight regex: " + regex);
			System.out.println("Char count (What it is...): " + content.length());
			System.out.println("Char count (What it looks like...): " + viewer.getTextWidget().getCharCount());
		}

		// Iterate over matches...
		for (Map.Entry<Integer, Integer> match : mlSet) {
			if (debug) {
				System.out.println("Found match: " + match);
			}

			// Get starting and finishing index of the match
			int startIdx = match.getKey();
			int endIdx = match.getValue();

			// Background color
			Color bgColor = new Color(null, bgRGB);

			// Foreground color
			Color fgColor = new Color(null, fgRGB);

			/*
			 * Create style range from the information above. Note that we pass
			 * the length of the highlighted match as the second argument to the
			 * constructor of StyleRange so we subtract the starting index from
			 * the end index here.
			 */
			StyleRange styleRange = new StyleRange(startIdx, endIdx - startIdx, fgColor, bgColor);

			// Make the text of the highlighted match bold and italic.
			styleRange.fontStyle = SWT.BOLD | SWT.ITALIC;

			// Add to list of style ranges
			styleRanges.add(styleRange);
		}

		// Apply all style ranges
		for (StyleRange sr : styleRanges) {
			styledText.setStyleRange(sr);
		}
	}

	/*
	 * In case something goes wrong, here's some feedback to the user.
	 */
	private void showError () {
		System.out.println("CDTFolder.showError()");

		// Grab shell of the current active workbench window ...
		Shell shell = CDTFolderPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

		// Create pop-up message
		MessageDialog.openError(shell, CDTFoldingConstants.NO_ACTION_CHOICE_TITLE,
				CDTFoldingConstants.NO_ACTION_CHOICE_TEXT);
	}

	/*
	 * Creating a helper map here, such that: map.get(i) = index of the first
	 * character in line i-1 of the entire content
	 */
	private Map<Integer, Integer> preProcess (String content) {
		Map<Integer, Integer> newLinePrefix = new HashMap<>();

		// Using line break as delimiter...
		char delimiter = '\n';

		int counter = 0;

		// Iterate over all character of the editor content
		for (int idx = 0; idx < content.length(); idx++) {
			if (content.charAt(idx) == delimiter) {

				// add entry to map if a line break is encountered
				newLinePrefix.put(++counter, idx);
			}
		}

		return newLinePrefix;
	}

	private String getCurrentEditorContent (ITextEditor editor) {
		// Grab underlying document of the editor
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());

		// Return the text in it
		return doc.get();
	}

	/*
	 * Based on a regular expression and the content of the file, the matches
	 * are computed right here.
	 */
	private Map<Integer, Integer> getMatchingLines (String regex, String content,
			Map<Integer, Integer> newLineMap) {

		// Compile the regular expression
		Pattern regexPattern = Pattern.compile(regex);

		// Create the corresponding matcher
		Matcher regexMatcher = regexPattern.matcher(content);

		Map<Integer, Integer> matchList = new HashMap<>();

		/*
		 * Let the matcher match... and for all matched found, add the start and
		 * end index to the local map...
		 */
		while (regexMatcher.find()) {
			matchList.put(regexMatcher.start(), regexMatcher.end());
		}

		return matchList;
	}

	/*
	 * Get folded sections. If there are separate matches in adjacent line we'll
	 * just create one annotation for all of that. For example: We have a match
	 * in line 14, another one in line 15 and another in line 16. Then we'll
	 * create one annotation that folds lines 14 to 16 - that also means if the
	 * user decides to unfold this annotation in his IDE, all three lines will
	 * be revealed.
	 */
	private ArrayList<FoldingSection> getFoldedSections (Map<Integer, Integer> newLineMap,
			Set<Map.Entry<Integer, Integer>> mlSet) {
		/*
		 * BitMap containing n entries, where n is the number of lines.
		 * lineBitSet.get(n) will be true if and only if line n-1 contains or
		 * partly contains a match for some user-specified regular expression.
		 */
		BitSet lineBitSet = new BitSet(newLineMap.size());

		for (Map.Entry<Integer, Integer> match : mlSet) {

			int endLine = getLineNr(match.getValue(), newLineMap);
			int startLine = getLineNr(match.getKey(), newLineMap);

			for (int i = startLine; i <= endLine; i++) {
				lineBitSet.set(i);
			}
		}

		ArrayList<FoldingSection> foldingSections = new ArrayList<>();

		for (int i = 0; i < lineBitSet.size(); i++) {
			int counter = i;

			if (lineBitSet.get(counter)) {
				int start = counter;

				while (lineBitSet.get(++counter)) {
				}

				int end = counter;

				foldingSections.add(new FoldingSection(start, end));

				i = end;
			}
		}

		return foldingSections;
	}

	/*
	 * Gets the line of the editor content that a character is in. To achieve
	 * this you pass the index of the character as the first argument and the
	 * line map as the second argument. We'll use the line map to perform
	 * binary search on it to return the corresponding source code line number.
	 */
	private int getLineNr (int idx, Map<Integer, Integer> newLineMap) {

		int b = 1;
		int e = newLineMap.size();

		while (b <= e) {
			int mid = (b + e) / 2;

			if (newLineMap.get(mid) <= idx && newLineMap.get(mid + 1) >= idx) {
				return mid + 1;
			} else if (newLineMap.get(mid) > idx) {
				e = mid - 1;
			} else {
				b = mid + 1;
			}
		}

		return e <= 0 ? 1 : newLineMap.size() - 1;
	}

	/*
	 * Get rid of all annotations added to the projection annotation model.
	 */
	@SuppressWarnings("unchecked")
	private void expand () {
		Iterator<ProjectionAnnotation> it = projectionAnnotationModel.getAnnotationIterator();

		while (it.hasNext()) {
			it.next().markExpanded();
			projectionAnnotationModel.removeAnnotation(it.next());
		}

		projectionAnnotationModel.removeAllAnnotations();
	}
}

class FoldingSection {
	private int startLine;
	private int endLine;

	public FoldingSection(int startLine, int endLine) {
		super();
		this.startLine = startLine;
		this.endLine = endLine;
	}

	public int getStartLine () {
		return startLine;
	}

	public void setStartLine (int startLine) {
		this.startLine = startLine;
	}

	public int getEndLine () {
		return endLine;
	}

	public void setEndLine (int endLine) {
		this.endLine = endLine;
	}
}
