package org.softwarepraktikum.plugin.cdtfolding;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.text.folding.ICFoldingStructureProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("restriction")
public class CDTFolder implements ICFoldingStructureProvider {

	ITextEditor editor;
	ProjectionViewer viewer;
	ProjectionAnnotationModel projectionAnnotationModel;

	public CDTFolder() {
		System.out.println("Constructor of CDT Folder called!");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void install(ITextEditor editor, ProjectionViewer viewer) {
		if (editor instanceof CEditor) {
			System.out.println("Calling install!");

			this.editor = editor;
			this.viewer = viewer;

			if (viewer != null) {
				System.out.println("Viewer isn't null.");
				projectionAnnotationModel = viewer
						.getProjectionAnnotationModel();

				// ProjectionAnnotation pa = new ProjectionAnnotation();

				if (projectionAnnotationModel != null) {
					System.out.println("PAM isn't null.");

					String regex = "\\bin[\\n]?t\\b";
					String content = getCurrentEditorContent();

					Map<Integer, Integer> newLineMap = preProcess(content);

					projectionAnnotationModel.removeAllAnnotations();

					for (Map.Entry<Integer, Integer> match : getMatchingLines(
							regex, content, newLineMap).entrySet()) {
						ProjectionAnnotation pa = new ProjectionAnnotation();

						int endLine = getLineNr(match.getValue(), newLineMap);
						int startLine = getLineNr(match.getKey(), newLineMap);

						int endIdx = newLineMap.get(endLine);
						int startIdx = newLineMap.get(startLine - 2);

						projectionAnnotationModel.addAnnotation(pa,
								new Position(startIdx, endIdx - startIdx));
						
						projectionAnnotationModel.collapse(pa);
						pa.markCollapsed();
						
						System.out.println("Adding annotation in line " + (startLine+1));
					}
				}
			}
		}
	}

	@Override
	public void uninstall() {
		System.out.println("Calling uninstall!");
	}

	@Override
	public void initialize() {
		System.out.println("Calling initialize!");
	}

	private Map<Integer, Integer> preProcess(String content) {
		Map<Integer, Integer> newLinePrefix = new HashMap<>();

		int counter = 0;

		for (int idx = 0; idx < content.length(); idx++) {
			if (content.charAt(idx) == '\n') {
				newLinePrefix.put(counter++, idx);
			}
		}

		return newLinePrefix;
	}

	private String getCurrentEditorContent() {
		final IEditorPart editor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (!(editor instanceof ITextEditor))
			return null;
		ITextEditor ite = (ITextEditor) editor;
		IDocument doc = ite.getDocumentProvider().getDocument(
				ite.getEditorInput());
		return doc.get();
	}

	private Map<Integer, Integer> getMatchingLines(String regex,
			String content, Map<Integer, Integer> newLineMap) {
		Pattern regexPattern = Pattern.compile(regex);

		Matcher regexMatcher = regexPattern.matcher(content);

		Map<Integer, Integer> matchList = new HashMap<>();

		while (regexMatcher.find()) {
			matchList.put(regexMatcher.start(), regexMatcher.end());
		}

		return matchList;
	}

	private int getLineNr(int idx, Map<Integer, Integer> newLineMap) {

		int b = 0;
		int e = newLineMap.size();

		while (b <= e) {
			int mid = (b + e) / 2;

			if (newLineMap.get(mid) <= idx && newLineMap.get(mid + 1) >= idx) {
				return mid + 2;
			} else if (newLineMap.get(mid) > idx) {
				e = mid - 1;
			} else {
				b = mid + 1;
			}
		}

		return -1;
	}
}
