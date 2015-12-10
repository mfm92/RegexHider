package org.softwarepraktikum.plugin.cdtfolding;

import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.text.folding.ICFoldingStructureProvider;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.texteditor.ITextEditor;

/*
 * TODO: Extension point: Debug, skip folded source code!
 */

@SuppressWarnings("restriction")
public class CDTFolderStructureProvider implements ICFoldingStructureProvider {

	ITextEditor editor;
	CDTFolderHighlighter folder = new CDTFolderHighlighter();
	
	static boolean called = false;
	
	ProjectionAnnotationModel projectionAnnotationModel;

	@Override
	public void install (final ITextEditor editor, ProjectionViewer viewer) {
		System.out.println("CDTFolderStructureProvider.install()");

		if (editor instanceof CEditor) {
			if (!called) {
				((CEditor) editor).addPostSaveListener((_$, _$$) -> folder.apply(editor, viewer));
			}

			called = true;
			this.editor = editor;
		}
	}

	@Override
	public void uninstall () {
		System.out.println("CDTFolderStructureProvider.uninstall()");
	}

	@Override
	public void initialize () {
		System.out.println("CDTFolderStructureProvider.initialize()");
	}
}