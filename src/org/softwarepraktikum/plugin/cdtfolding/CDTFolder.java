package org.softwarepraktikum.plugin.cdtfolding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

	public void expand() {
		projectionAnnotationModel.removeAllAnnotations();
	}

	public void collapse(ITextEditor editor,
			ProjectionAnnotationModel projectionAnnotationModel) {

		if (projectionAnnotationModel != null) {
			this.projectionAnnotationModel = projectionAnnotationModel;
			text = new ArrayList<>();

			IPreferenceStore store = CDTFolderPlugin.getDefault().getPreferenceStore();
			String regex = store.getString(CDTFoldingConstants.TF_REGEX_KEY_STR).
					split(CDTFoldingConstants.REGEX_SEPARATOR)[0];
			
			System.out.println("CDTFolder.collapse()");

			if (debug) {
				System.out.println("The regex is: " + store.getString(CDTFoldingConstants.TF_REGEX_KEY_STR));
				
				System.out.println("Separated: ");
				
				for (String r : store.getString(CDTFoldingConstants.TF_REGEX_KEY_STR)
						.split(CDTFoldingConstants.REGEX_SEPARATOR)) {
					System.out.println(r);
				}
				
				System.out.println("Final choice: " + regex);
			}

			String content = getCurrentEditorContent(editor);

			Map<Integer, Integer> newLineMap = preProcess(content);
			
			removeProjectionAnnotations(regex);

			int counter = 0;

			for (Map.Entry<Integer, Integer> match : getMatchingLines(regex,
					content, newLineMap).entrySet()) {
				ProjectionAnnotation pa = new ProjectionAnnotation();
				
				int endLine = getLineNr(match.getValue(), newLineMap);
				int startLine = getLineNr(match.getKey(), newLineMap);
				
				int endIdx = endLine + 1 > newLineMap.size() ? content.length() : 
					(newLineMap.get(startLine + 1));
				
				int startIdx = newLineMap.get(startLine == 1 ? startLine : startLine - 1);

				pa.setText(text.get(counter++));

				projectionAnnotationModel.addAnnotation(pa, new Position(
						startIdx, endIdx - startIdx));

				projectionAnnotationModel.collapse(pa);
				pa.markCollapsed();

				if (debug) {
					System.out.println("Adding annotation in line " + startLine);
				}
			}
		}
	}
	
	private void removeProjectionAnnotations(String regex) {
		ArrayList<ProjectionAnnotation> annotationsToRemove = new ArrayList<>();
		
		@SuppressWarnings("unchecked")
		Iterator<ProjectionAnnotation> paIt = projectionAnnotationModel
				.getAnnotationIterator();

		while (paIt.hasNext()) {
			ProjectionAnnotation a = paIt.next();

			if (debug) {
				System.out.format("Current annotation: %s", a.getText());
				
				Pattern regexPattern = Pattern.compile(regex);

				Matcher regexMatcher = regexPattern.matcher(a.getText());
				
				if (!regexMatcher.find() || true) {
					annotationsToRemove.add(a);
				}
			}
		}

		for (ProjectionAnnotation pa : annotationsToRemove) {
			projectionAnnotationModel.removeAnnotation(pa);
		}
	}

	private Map<Integer, Integer> preProcess(String content) {
		Map<Integer, Integer> newLinePrefix = new HashMap<>();

		int counter = 0;

		for (int idx = 0; idx < content.length(); idx++) {
			if (content.charAt(idx) == '\n') {
				newLinePrefix.put(++counter, idx);
			}
		}

		return newLinePrefix;
	}

	private String getCurrentEditorContent(ITextEditor editor) {
		IDocument doc = editor.getDocumentProvider().getDocument(
				editor.getEditorInput());
		return doc.get();
	}

	private Map<Integer, Integer> getMatchingLines(String regex,
			String content, Map<Integer, Integer> newLineMap) {
		Pattern regexPattern = Pattern.compile(regex);

		Matcher regexMatcher = regexPattern.matcher(content);

		Map<Integer, Integer> matchList = new HashMap<>();

		while (regexMatcher.find()) {
			matchList.put(regexMatcher.start(), regexMatcher.end());
			text.add(regexMatcher.group());
		}

		return matchList;
	}

	private int getLineNr(int idx, Map<Integer, Integer> newLineMap) {

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
