package cosy.sbg.softwarepraktikum.plugin.reditor;

import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.text.ICPartitions;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Extension of CEditor.
 * The only difference to the CEditor is that the RegexEditor uses a slightly altered SourceViewerConfiguration.
 * (see RegexSourceViewerConfiguration for more)
 */
@SuppressWarnings("restriction")
public class RegexEditor extends CEditor {
	public RegexEditor() {
		super();
		setSourceViewerConfiguration(new RegexSourceViewerConfiguration(CUIPlugin.getDefault().getTextTools().getColorManager(), CUIPlugin.getDefault().getPreferenceStore(), this, ICPartitions.C_PARTITIONING));
	}
	
	@Override
	protected void initializeEditor () {
		setSourceViewerConfiguration(new RegexSourceViewerConfiguration(CUIPlugin.getDefault().getTextTools().getColorManager(), CUIPlugin.getDefault().getPreferenceStore(), this, ICPartitions.C_PARTITIONING));
	}
	
	@Override
	protected void setPreferenceStore(IPreferenceStore store) {
		super.setPreferenceStore(store);
	}
}
