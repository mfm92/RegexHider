package org.softwarepraktikum.plugin.cdtfolding;

import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.text.folding.ICFoldingStructureProvider;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.texteditor.ITextEditor;

/*
 * TODO: Extension point: Debug, skip folded source code!
 */
@SuppressWarnings("restriction")
public class CDTFolderStructureProvider implements ICFoldingStructureProvider {

	ITextEditor editor;
	CDTFolder folder;

	ProjectionListener projectionListener;

	ProjectionAnnotationModel projectionAnnotationModel;

	private final class ProjectionListener implements IProjectionListener {
		private ProjectionViewer viewer;

		/**
		 * Registers the listener with the viewer.
		 * 
		 * @param viewer
		 *            the viewer to register a listener with
		 */
		public ProjectionListener(ProjectionViewer viewer) {
			System.out.println("CDTFolderStructureProvider.ProjectionListener.ProjectionListener()");

			this.viewer = viewer;
			viewer.addProjectionListener(this);
		}

		/**
		 * Disposes of this listener and removes the projection listener from
		 * the viewer.
		 */
		public void dispose () {
			System.out.println("CDTFolderStructureProvider.ProjectionListener.dispose()");

			if (viewer != null) {
				viewer.removeProjectionListener(this);
				viewer = null;
			}
		}

		@Override public void projectionEnabled () {}
		@Override public void projectionDisabled () {}
	}

	@Override
	public void install (final ITextEditor editor, ProjectionViewer viewer) {
		System.out.println("CDTFolderStructureProvider.install()");

		folder = new CDTFolder();

		if (editor != null) {
			if (projectionListener != null) {
				this.projectionListener.dispose();
				projectionListener = null;
			}

			this.editor = null;
		}
		
		((CEditor) editor).addPostSaveListener(($, $$) -> folder.collapse(editor, viewer));
		
		this.editor = editor;
		this.projectionListener = new ProjectionListener(viewer);
	}

	@Override
	public void uninstall () {
		System.out.println("CDTFolderStructureProvider.uninstall()");

		if (editor != null) {
			this.projectionListener.dispose();
			projectionListener = null;
			this.editor = null;
		}
	}

	@Override
	public void initialize () {
		System.out.println("CDTFolderStructureProvider.initialize()");
	}
}