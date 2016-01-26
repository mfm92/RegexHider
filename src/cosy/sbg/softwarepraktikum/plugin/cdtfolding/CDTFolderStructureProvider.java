package cosy.sbg.softwarepraktikum.plugin.cdtfolding;

import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.internal.ui.editor.IPostSaveListener;
import org.eclipse.cdt.ui.text.folding.ICFoldingStructureProvider;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.texteditor.ITextEditor;

import cosy.sbg.softwarepraktikum.plugin.reditor.RegexEditor;

@SuppressWarnings("restriction")
public class CDTFolderStructureProvider implements ICFoldingStructureProvider {

	ITextEditor editor;

	CDTFolderHighlighter folder = new CDTFolderHighlighter();

	ProjectionAnnotationModel projectionAnnotationModel;

	IPostSaveListener postSaveListener;

	/**
	 * If the user opens a file with C source code, install a listener that
	 * triggers folding/highlighting whenever the user saves the editor content.
	 */
	@Override
	public void install (final ITextEditor editor, ProjectionViewer viewer) {
		System.out.println("CDTFolderStructureProvider.install()");

		IPostSaveListener postSaveListener = (t, m) -> folder.apply(editor, viewer);
	
		if (editor instanceof CEditor) {
			((CEditor) editor).removePostSaveListener(postSaveListener);
			((CEditor) editor).addPostSaveListener(postSaveListener);

			this.editor = editor;
			this.postSaveListener = postSaveListener;
		}

		if (editor instanceof RegexEditor) {
			((RegexEditor) editor).removePostSaveListener(postSaveListener);
			((RegexEditor) editor).addPostSaveListener(postSaveListener);
			
			this.editor = editor;
			this.postSaveListener = postSaveListener;
		}
	}

	/**
	 * Remove the listener whenever the user closes the source code file.
	 */
	@Override
	public void uninstall () {
		System.out.println("CDTFolderStructureProvider.uninstall()");

		if (editor instanceof CEditor) {
			((CEditor) editor).removePostSaveListener(postSaveListener);
		} else if (editor instanceof RegexEditor) {
			((RegexEditor) editor).removePostSaveListener(postSaveListener);
		}
	}

	@Override
	public void initialize () {
		System.out.println("CDTFolderStructureProvider.initialize()");
	}
}