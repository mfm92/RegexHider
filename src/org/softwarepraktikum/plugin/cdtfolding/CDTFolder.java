package org.softwarepraktikum.plugin.cdtfolding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.ui.texteditor.ITextEditor;
import org.softwarepraktikum.plugin.CDTFolderPlugin;

public class CDTFolder {

	ProjectionAnnotationModel projectionAnnotationModel;
	ArrayList<String> text;

	boolean debug = true;

	public void expand () {
		projectionAnnotationModel.removeAllAnnotations();
	}

	public void collapse (ITextEditor editor, ProjectionAnnotationModel projectionAnnotationModel) {
		if (projectionAnnotationModel != null) {
			this.projectionAnnotationModel = projectionAnnotationModel;

			IPreferenceStore store = CDTFolderPlugin.getDefault().getPreferenceStore();
			String[] regexes = store.getString(CDTFoldingConstants.TF_REGEX_KEY_STR).split(
					CDTFoldingConstants.REGEX_SEPARATOR);

			System.out.println("CDTFolder.collapse()");
			
			projectionAnnotationModel.removeAllAnnotations();

			for (String regex : regexes) {
				text = new ArrayList<>();

				if (debug) {
					System.out.println("Current regex is: " + regex);
				}

				String content = getCurrentEditorContent(editor);

				Map<Integer, Integer> newLineMap = preProcess(content);

				int counter = 0;

				for (Map.Entry<Integer, Integer> match : getMatchingLines(regex, content, newLineMap)
						.entrySet()) {
					ProjectionAnnotation pa = new ProjectionAnnotation();

					int endLine = getLineNr(match.getValue(), newLineMap);
					int startLine = getLineNr(match.getKey(), newLineMap);

					int endIdx = endLine + 1 > newLineMap.size() ? content.length() : (newLineMap
							.get(startLine + 1));

					int startIdx = newLineMap.get(startLine == 1 ? startLine : startLine - 1);

					pa.setText(text.get(counter++));

					projectionAnnotationModel.addAnnotation(pa, new Position(startIdx, endIdx - startIdx));

					projectionAnnotationModel.collapse(pa);
					pa.markCollapsed();

					if (debug) {
						System.out.println("Adding annotation in line " + startLine);
					}
				}
			}
		}
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
			text.add(regexMatcher.group());
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
