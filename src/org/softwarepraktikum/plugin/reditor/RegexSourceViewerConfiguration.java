package org.softwarepraktikum.plugin.reditor;

import org.eclipse.cdt.ui.text.CSourceViewerConfiguration;
import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

public class RegexSourceViewerConfiguration extends CSourceViewerConfiguration {
	public RegexSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}

	@Override
	public IReconciler getReconciler (ISourceViewer sourceViewer) {
		IReconciler reconciler = super.getReconciler(sourceViewer);
		
		if (reconciler != null && reconciler instanceof MonoReconciler) {
			((MonoReconciler) reconciler).setDelay(1000000);
		}
		
		return reconciler;
	}
}