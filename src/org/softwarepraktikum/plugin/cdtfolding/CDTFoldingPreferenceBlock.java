package org.softwarepraktikum.plugin.cdtfolding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.swt.widgets.Text;
import org.softwarepraktikum.plugin.CDTFolderPlugin;

public class CDTFoldingPreferenceBlock implements ICFoldingPreferenceBlock {

	IPreferenceStore store;
	CDTFoldingChildPreferenceStore.FoldingKey[] foldingKeys;
	CDTFoldingChildPreferenceStore childPreferenceStore;

	protected Map<Button, String> checkBoxes = new HashMap<Button, String>();
	
	private SelectionListener checkBoxListener = new SelectionListener() {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			System.out.println("SelectionListener::WidgetDefaultSelected(SelectionEvent) called!");
		}

		@Override
		public void widgetSelected(SelectionEvent event) {
			System.out.println("SelectionListener::WidgetSelected(SelectionEvent) called!");
			Button button = (Button) event.widget;
			String key = checkBoxes.get(button);
			childPreferenceStore.setValue(key, button.getSelection());
		}
	};
	
	public CDTFoldingPreferenceBlock() {
		System.out.println("CDTFoldingPreferenceBlock::CDTFoldingPreferenceBlock() called!");
		
		store = CDTFolderPlugin.getDefault().getPreferenceStore();
		fillOverlayMap();
		childPreferenceStore = new CDTFoldingChildPreferenceStore(store, foldingKeys);
	}

	private void fillOverlayMap() {
		ArrayList<CDTFoldingChildPreferenceStore.FoldingKey> foldingKeys = new ArrayList<>();

		foldingKeys.add(new CDTFoldingChildPreferenceStore.FoldingKey(
				CDTFoldingChildPreferenceStore.TD_BOOLEAN, CDTFoldingConstants.TF_REGEX_KEY_BOOL));
		foldingKeys.add(new CDTFoldingChildPreferenceStore.FoldingKey(
				CDTFoldingChildPreferenceStore.TD_STRING, CDTFoldingConstants.TF_REGEX_KEY_STR));

		this.foldingKeys = foldingKeys.toArray
				(new CDTFoldingChildPreferenceStore.FoldingKey[foldingKeys.size()]);
	}

	@Override
	public Control createControl(Composite parent) {
		System.out.println("CDTFoldingPreferenceBlock::CreateControl(Composite) called!");

		childPreferenceStore.load();
		childPreferenceStore.start();

		Composite inner = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		
		layout.verticalSpacing = 3;
		layout.marginWidth = 0;
		inner.setLayout(layout);
		
		addCheckBox(inner, CDTFoldingConstants.TF_CHECKBOX_NAME, CDTFoldingConstants.TF_REGEX_KEY_BOOL);
		
		ControlFactory.createEmptySpace(inner);
		
		Composite group = ControlFactory.createGroup(inner, CDTFoldingConstants.TF_CHECKBOX_NAME, 2);
		
		addTextField(group, CDTFoldingConstants.TF_REGEX_KEY_STR);

		return inner;
	}
	
	private Button addCheckBox(Composite composite, String label, String key) {
		Button b = ControlFactory.createCheckBox(composite, CDTFoldingConstants.TF_CHECKBOX_NAME);
		b.addSelectionListener(checkBoxListener);
		checkBoxes.put(b, key);

		return b;
	}
	
	private Text addTextField(Composite composite, String key) {
		Text t = ControlFactory.createTextField(composite);
		
		t.setText(store.getString(CDTFoldingConstants.TF_REGEX_KEY_STR));
		t.setSize(500, 80);
		t.addModifyListener(e -> childPreferenceStore.setValue(key, t.getText()));
		
		return t;
	}

	@Override
	public void initialize() {
		System.out.println("CDTFoldingPreferenceBlock::Initialize() called!");
		
		childPreferenceStore.addKeys(foldingKeys);
		
		for (Button button : checkBoxes.keySet()) {
			button.setSelection(childPreferenceStore.getBoolean(checkBoxes.get(button)));
		}
	}

	@Override
	public void performOk() {
		System.out.println("CDTFoldingPreferenceBlock::PerformOK() called!");
		
		childPreferenceStore.propagate();
	}

	@Override
	public void performDefaults() {
		System.out.println("CDTFoldingPreferenceBlock::PerformDefaults() called!");

		childPreferenceStore.loadDefaults();
		
		for (Button button : checkBoxes.keySet()) {
			button.setSelection(childPreferenceStore.getBoolean(checkBoxes.get(button)));
		}
	}

	@Override
	public void dispose() {
		System.out.println("CDTFoldingPreferenceBlock::Dispose() called!");
		childPreferenceStore.stop();
	}
}