package org.softwarepraktikum.plugin.cdtfolding;

import java.util.ArrayList;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
import org.softwarepraktikum.plugin.CDTFolderPlugin;

public class CDTFolder {

	enum ActionChoice {
		Folding, Highlighting, Error
	}
	
	ProjectionAnnotationModel projectionAnnotationModel;

	boolean debug = true;

	@SuppressWarnings("unchecked")
	public void expand () {		
		Iterator<ProjectionAnnotation> it = projectionAnnotationModel.getAnnotationIterator();
		
		while (it.hasNext()) {
			it.next().markExpanded();
			projectionAnnotationModel.removeAnnotation(it.next());
		}
		
		projectionAnnotationModel.removeAllAnnotations();
	}

	public void collapse (ITextEditor editor, ProjectionViewer viewer) {
		System.out.println("CDTFolder.collapse()");

		if (viewer.getProjectionAnnotationModel() != null) {
			this.projectionAnnotationModel = viewer.getProjectionAnnotationModel();
			editor.doRevertToSaved();
			expand();

			IPreferenceStore store = CDTFolderPlugin.getDefault().getPreferenceStore();
			String regex = store.getString(CDTFoldingConstants.CHECKED_STRING_INPUT);
			String choiceID = store.getString(CDTFoldingConstants.COMBO_CHOICE);			
			ActionChoice actionChoice = 
					choiceID.equals(CDTFoldingConstants.COMBO_CHOICE_FOLD) ? ActionChoice.Folding : 
					choiceID.equals(CDTFoldingConstants.COMBO_CHOICE_HIGHLIGHT) ? ActionChoice.Highlighting :
						ActionChoice.Error;
			
			if (debug) {
				System.out.println("Current regex is: " + regex);
				System.out.println("ActionChoice is: " + actionChoice);
			}
			
			String content = getCurrentEditorContent(editor);
			
			switch (actionChoice) {
				case Folding: fold(regex, content); break;
				case Highlighting: highlight(regex, content, viewer); break;
				case Error: showError(); break;
			}
		} else {
			System.out.println("ProjectionAnnotationModel is null!");
		}
	}
	
	private void fold(String regex, String content) {
		System.out.println("CDTFolder.fold()");
		Map<Integer, Integer> newLineMap = preProcess(content);

		Set<Map.Entry<Integer, Integer>> mlSet = getMatchingLines(regex, content, newLineMap).entrySet();
		
		for (Map.Entry<Integer, Integer> match : mlSet) {

			ProjectionAnnotation pa = new ProjectionAnnotation();

			int endLine = getLineNr(match.getValue(), newLineMap);
			int startLine = getLineNr(match.getKey(), newLineMap);

			int endIdx = endLine + 1 > newLineMap.size() ? content.length() : (newLineMap
					.get(startLine + 1));

			int startIdx = newLineMap.get(startLine == 1 ? startLine : startLine - 1);

			projectionAnnotationModel.addAnnotation(pa, new Position(startIdx, endIdx - startIdx));

			projectionAnnotationModel.collapse(pa);
			pa.markCollapsed();
		}
	}
	
	private void highlight (String regex, String content, ProjectionViewer viewer) {
		System.out.println("CDTFolder.highlight()");
		Map<Integer, Integer> newLineMap = preProcess(content);
		ArrayList<StyleRange> styleRanges = new ArrayList<>();
		
		if (viewer.getTextWidget().getCharCount() != content.length()) {
			viewer.getTextWidget().setText(content);
		}
		
		IPreferenceStore store = CDTFolderPlugin.getDefault().getPreferenceStore();

		RGB bgRGB = CDTUtilities.restoreRGB(store.getString(CDTFoldingConstants.COLOR_PICKED_BG));
		RGB fgRGB = CDTUtilities.restoreRGB(store.getString(CDTFoldingConstants.COLOR_PICKED_FG));
		
		Set<Map.Entry<Integer, Integer>> mlSet = getMatchingLines(regex, content, newLineMap).entrySet();
		
		if (debug) {
			System.out.println("Content: " + content);
			System.out.println("Highlight regex: " + regex);
			System.out.println("Char count (What it is...): " + content.length());
			System.out.println("Char count (What it looks like...): " + viewer.getTextWidget().getCharCount());
		}
		
		for (Map.Entry<Integer, Integer> match : mlSet) {
			if (debug) {
				System.out.println("Found match: " + match);
			}
			
			StyleRange styleRange = new StyleRange(match.getKey(), match.getValue() - match.getKey(), 
					new Color(null, fgRGB), new Color(null, bgRGB));
			styleRange.fontStyle = SWT.BOLD | SWT.ITALIC;
			styleRanges.add(styleRange);
			
			viewer.setTextColor(styleRange.foreground, match.getKey(), styleRange.length, true);
		}
		
		for (StyleRange sr : styleRanges) {
			viewer.getTextWidget().setStyleRange(sr);
		}
	}
	
	private void showError () {
		System.out.println("CDTFolder.showError()");
		Shell shell = CDTFolderPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		
		MessageDialog.openError(shell, CDTFoldingConstants.NO_ACTION_CHOICE_TITLE,
				CDTFoldingConstants.NO_ACTION_CHOICE_TEXT);
	}
	
	private Map<Integer, Integer> preProcess (String content) {
		Map<Integer, Integer> newLinePrefix = new HashMap<>();

		int counter = 0;

		for (int idx = 0; idx < content.length(); idx++) {
			if (content.charAt(idx) == '\n') {
				newLinePrefix.put(++counter, idx);
			}
		}

		return newLinePrefix;
	}

	private String getCurrentEditorContent (ITextEditor editor) {
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		return doc.get();
	}

	private Map<Integer, Integer> getMatchingLines (String regex, String content,
			Map<Integer, Integer> newLineMap) {
		Pattern regexPattern = Pattern.compile(regex);

		Matcher regexMatcher = regexPattern.matcher(content);

		Map<Integer, Integer> matchList = new HashMap<>();

		while (regexMatcher.find()) {
			matchList.put(regexMatcher.start(), regexMatcher.end());
		}

		return matchList;
	}

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
}