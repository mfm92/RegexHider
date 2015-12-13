package org.softwarepraktikum.plugin.cdtfolding;

import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.text.folding.ICFoldingStructureProvider;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.texteditor.ITextEditor;
import org.softwarepraktikum.plugin.reditor.RegexEditor;

/*
 * TODO: Extension point: Debug, skip folded source code!
 */

@SuppressWarnings("restriction")
public class CDTFolderStructureProvider implements ICFoldingStructureProvider {

	ITextEditor editor;
	CDTFolderHighlighter folder = new CDTFolderHighlighter();
	
	static boolean cCalled = false;
	static boolean regexCalled = false;
	
	ProjectionAnnotationModel projectionAnnotationModel;

	@Override
	public void install (final ITextEditor editor, ProjectionViewer viewer) {
		System.out.println("CDTFolderStructureProvider.install()");

		if (editor instanceof CEditor) {
			if (!cCalled) { 
				((CEditor) editor).addPostSaveListener((_$, _$$) -> folder.apply(editor, viewer));
			}

			cCalled = true;
			this.editor = editor;
		}
		
		if (editor instanceof RegexEditor) {
			if (!regexCalled) {
				((RegexEditor) editor).addPostSaveListener((_$, _$$) -> folder.apply(editor, viewer));
			}

			regexCalled = true;
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