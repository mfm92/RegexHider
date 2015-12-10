package org.softwarepraktikum.plugin.reditor;

import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.text.ICPartitions;
import org.eclipse.jface.preference.IPreferenceStore;

@SuppressWarnings("restriction")
public class RegexEditor extends CEditor {
	
	public RegexEditor() {
		super();
		System.out.println("RegexEditor.RegexEditor()");
		setSourceViewerConfiguration(new RegexSourceViewerConfiguration(CUIPlugin.getDefault().getTextTools().getColorManager(), CUIPlugin.getDefault().getPreferenceStore(), this, ICPartitions.C_PARTITIONING));
	}
	
	@Override
	protected void initializeEditor () {
		System.out.println("RegexEditor.initializeEditor()");
		setSourceViewerConfiguration(new RegexSourceViewerConfiguration(CUIPlugin.getDefault().getTextTools().getColorManager(), CUIPlugin.getDefault().getPreferenceStore(), this, ICPartitions.C_PARTITIONING));
	}
	
	@Override
	protected void setPreferenceStore(IPreferenceStore store) {
		System.out.println("RegexEditor.setPreferenceStore()");
		super.setPreferenceStore(store);
	}
}
