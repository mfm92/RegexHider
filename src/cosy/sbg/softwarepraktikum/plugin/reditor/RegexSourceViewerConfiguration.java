package cosy.sbg.softwarepraktikum.plugin.reditor;

import org.eclipse.cdt.ui.text.CSourceViewerConfiguration;
import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

/*
 * Source Viewer Configuration used to configure the RegexEditor (which extends
 * CEditor)
 */
public class RegexSourceViewerConfiguration extends CSourceViewerConfiguration {
	public RegexSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}

	/**
	 * Override getReconcile of the CSourceViewer Configuration. Increasing the
	 * delay of the created MonoReconciler from 500 to 1000000. This delay
	 * determines the amount of time that passes between applying the
	 * highlighting of the matches of some user-specified regex and syntax
	 * coloring overriding those highlightings.
	 */
	@Override
	public IReconciler getReconciler (ISourceViewer sourceViewer) {
		IReconciler reconciler = super.getReconciler(sourceViewer);

		if (reconciler != null && reconciler instanceof MonoReconciler) {
			((MonoReconciler) reconciler).setDelay(1000000);
		}

		return reconciler;
	}
}