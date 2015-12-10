package org.softwarepraktikum.plugin.reditor;

import org.eclipse.cdt.internal.core.model.ProgressMonitorAndCanceler;
import org.eclipse.cdt.internal.ui.text.CCompositeReconcilingStrategy;
import org.eclipse.cdt.internal.ui.text.CReconciler;
import org.eclipse.cdt.ui.text.CSourceViewerConfiguration;
import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("restriction")
public class RegexSourceViewerConfiguration extends CSourceViewerConfiguration {
	public RegexSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}

	@Override
	public IReconciler getReconciler (ISourceViewer sourceViewer) {
		if (fTextEditor != null) {
			CCompositeReconcilingStrategy strategy = new CCompositeReconcilingStrategy(sourceViewer,
					fTextEditor, getConfiguredDocumentPartitioning(sourceViewer));
			
			MonoReconciler reconciler = new CReconciler(fTextEditor, strategy);
			
			reconciler.setIsIncrementalReconciler(false);
			reconciler.setProgressMonitor(new ProgressMonitorAndCanceler());
			reconciler.setDelay(1000000);
			
			return reconciler;
		}
		return null;
	}
}