package org.softwarepraktikum.plugin.cdtfolding;

import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.text.folding.ICFoldingStructureProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("restriction")
public class CDTFolderStructureProvider implements ICFoldingStructureProvider {

	ITextEditor editor;
	IPreferenceStore parent;
	CDTFolder folder;
	
	ProjectionListener projectionListener;
	
	ProjectionAnnotationModel projectionAnnotationModel;
	
	private final class ProjectionListener implements IProjectionListener {
		private ProjectionViewer viewer;

		/**
		 * Registers the listener with the viewer.
		 * 
		 * @param viewer the viewer to register a listener with
		 */
		public ProjectionListener(ProjectionViewer viewer) {
			System.out.println("ProjectionListener::ProjectionListener(ProjectionViewer) called!");
			
			this.viewer = viewer;
			viewer.addProjectionListener(this);
		}
		
		/**
		 * Disposes of this listener and removes the projection listener from the viewer.
		 */
		public void dispose() {
			System.out.println("ProjectionListener::Dispose() called!");
			
			if (viewer != null) {
				viewer.removeProjectionListener(this);
				viewer = null;
			}
		}
		
		/*
		 * @see org.eclipse.jface.text.source.projection.IProjectionListener#projectionEnabled()
		 */
		@Override
		public void projectionEnabled() {
			System.out.println("ProjectionListener::ProjectionEnabled() called!");
			folder.collapse(editor, viewer.getProjectionAnnotationModel());
		}

		/*
		 * @see org.eclipse.jface.text.source.projection.IProjectionListener#projectionDisabled()
		 */
		@Override
		public void projectionDisabled() {
			System.out.println("ProjectionListener::ProjectionDisabled() called!");
			folder.expand();
		}
	}

	@Override public void install (final ITextEditor editor, ProjectionViewer viewer) {
		
		System.out.println("CDTFolderStructureProvider::Install(ITextEditor, ProjectionViewer) called!");
		
		folder = new CDTFolder();
		
		if (editor != null) {
			if (projectionListener != null) {
				this.projectionListener.dispose();
				projectionListener = null;
			}
			
			this.editor = null;
		}
		
		if (editor instanceof CEditor) {
			this.editor = editor;
			this.projectionListener = new ProjectionListener(viewer);
		}
	}

	@Override
	public void uninstall() {
		System.out.println("CDTFolderStructureProvider::Uninstall() called!");
		
		if (editor != null) {
			this.projectionListener.dispose();
			projectionListener = null;
			this.editor = null;
		}
	}

	@Override
	public void initialize() {
		System.out.println("CDTFolderStructureProvider::Initialize() called!");
	}
}
