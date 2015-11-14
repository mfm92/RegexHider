package org.softwarepraktikum.plugin.cdtfolding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.ui.PreferenceConstants;
import org.eclipse.cdt.ui.text.folding.ICFoldingPreferenceBlock;
import org.eclipse.cdt.utils.ui.controls.ControlFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwarepraktikum.plugin.Activator;

public class CDTFoldingPreferenceBlock implements ICFoldingPreferenceBlock {

	IPreferenceStore store;
	CDTFoldingChildPreferenceStore.FoldingKey[] foldingMap;
	CDTFoldingChildPreferenceStore childPreferenceStore;

	protected Map<Button, String> checkBoxes = new HashMap<Button, String>();
	private Button inactiveCDTFoldingButton;
	
	private SelectionListener fCheckBoxListener = new SelectionListener() {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			Button button = (Button) e.widget;
			String key = checkBoxes.get(button);
			childPreferenceStore.setValue(key, button.getSelection());
			updateEnablement(key);
		}
	};
	
	public CDTFoldingPreferenceBlock() {
		store = Activator.getDefault().getPreferenceStore();
		fillOverlayMap();
		
		childPreferenceStore = new CDTFoldingChildPreferenceStore(store, foldingMap);
	}

	private void fillOverlayMap() {
		ArrayList<CDTFoldingChildPreferenceStore.FoldingKey> foldingKeys = new ArrayList<>();

		foldingKeys.add(new CDTFoldingChildPreferenceStore.FoldingKey(
				CDTFoldingChildPreferenceStore.TD_BOOLEAN, PreferenceConstants.EDITOR_FOLDING_HEADERS));

		foldingMap = foldingKeys
				.toArray(new CDTFoldingChildPreferenceStore.FoldingKey[foldingKeys
						.size()]);
	}

	@Override
	public Control createControl(Composite parent) {
		System.out.println("CreateControl called!");

		childPreferenceStore.load();
		childPreferenceStore.start();

		Composite inner = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		
		layout.verticalSpacing = 3;
		layout.marginWidth = 0;
		inner.setLayout(layout);
		
		addCheckBox(inner, "Test", "Key");
		
		ControlFactory.createEmptySpace(inner);
		
		Composite group = ControlFactory.createGroup(inner, "Bla", 1);
		
		ControlFactory.createTextField(group);

		return inner;
	}
	
	private Button addCheckBox(Composite composite, String label, String key) {
		Button b = ControlFactory.createCheckBox(composite, "test");
		b.addSelectionListener(fCheckBoxListener);
		checkBoxes.put(b, key);

		return b;
	}
	
	protected void updateEnablement(String key) {
		if (inactiveCDTFoldingButton != null &&
				PreferenceConstants.EDITOR_FOLDING_PREPROCESSOR_BRANCHES_ENABLED.equals(key)) {
			inactiveCDTFoldingButton.setEnabled(childPreferenceStore.getBoolean(key));
		}
	}

	@Override
	public void initialize() {
		System.out.println("Initialize called!");
		
		for (Button button : checkBoxes.keySet()) {
			button.setSelection(childPreferenceStore.getBoolean(checkBoxes.get(button)));
		}
	}

	@Override
	public void performOk() {
		System.out.println("PerformOK called!");
		
		childPreferenceStore.propagate();
	}

	@Override
	public void performDefaults() {
		System.out.println("PerformDefaults called!");

		childPreferenceStore.loadDefaults();
		
		for (Button button : checkBoxes.keySet()) {
			button.setSelection(childPreferenceStore.getBoolean(checkBoxes.get(button)));
		}
	}

	@Override
	public void dispose() {
		System.out.println("Dispose called!");
	}
}