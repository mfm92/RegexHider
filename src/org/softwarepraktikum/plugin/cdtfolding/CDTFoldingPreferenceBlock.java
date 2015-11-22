package org.softwarepraktikum.plugin.cdtfolding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.cdt.ui.text.folding.ICFoldingPreferenceBlock;
import org.eclipse.cdt.utils.ui.controls.ControlFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.softwarepraktikum.plugin.CDTFolderPlugin;

public class CDTFoldingPreferenceBlock implements ICFoldingPreferenceBlock {

	enum Move {
		UP, DOWN
	};
	
	IPreferenceStore store;

	CDTFoldingChildPreferenceStore.FoldingKey[] foldingKeys;
	CDTFoldingChildPreferenceStore childPreferenceStore;
	
	CheckboxTableViewer cbtViewer;
	
	ArrayList<String> regexes = new ArrayList<>();
	
	Label errorLabel;
	Label currentRegexLabel;
	
	public CDTFoldingPreferenceBlock() {
		System.out
				.println("CDTFoldingPreferenceBlock.CDTFoldingPreferenceBlock()");

		store = CDTFolderPlugin.getDefault().getPreferenceStore();
		fillOverlayMap();
		childPreferenceStore = new CDTFoldingChildPreferenceStore(store,
				foldingKeys);
	}

	private void fillOverlayMap() {
		ArrayList<CDTFoldingChildPreferenceStore.FoldingKey> foldingKeys = new ArrayList<>();

		foldingKeys.add(new CDTFoldingChildPreferenceStore.FoldingKey(
				CDTFoldingChildPreferenceStore.TD_STRING,
				CDTFoldingConstants.TF_REGEX_KEY_STR));
		
		foldingKeys.add(new CDTFoldingChildPreferenceStore.FoldingKey(
				CDTFoldingChildPreferenceStore.TD_STRING,
				CDTFoldingConstants.ENTIRE_INPUT));

		this.foldingKeys = foldingKeys
				.toArray(new CDTFoldingChildPreferenceStore.FoldingKey[foldingKeys
						.size()]);
	}

	@Override
	public Control createControl(Composite parent) {
		System.out.println("CDTFoldingPreferenceBlock.createControl()");

		childPreferenceStore.load();
		childPreferenceStore.start();

		Composite inner = new Composite(parent, SWT.NONE);
		setLayout(inner);

		Composite group = ControlFactory.createGroup(inner,
				CDTFoldingConstants.TF_TEXTFIELD_NAME, 1);

		Text t = addTextField(group);

		ControlFactory.createEmptySpace(inner);

		cbtViewer = addCBTViewer(inner);
		
		Composite editButtons = ControlFactory.createComposite(group, 4);
		
		addButton(editButtons, t);
		removeButton(editButtons);
		
		moveButton(editButtons, Move.UP);
		moveButton(editButtons, Move.DOWN);
		
		addErrorLabel(inner);
		currentRegexLabel(inner);

		return inner;
	}
	
	private void setLayout (Composite parent) {
		GridLayout layout = new GridLayout(1, true);

		layout.verticalSpacing = 3;
		layout.marginWidth = 0;
		parent.setLayout(layout);
	}
	
	private CheckboxTableViewer addCBTViewer (Composite parent) {
		CheckboxTableViewer cbtViewer = ControlFactory.createListViewer(parent,
				new String[]{}, 420, 300, SWT.V_SCROLL);
		cbtViewer.setContentProvider(new ArrayContentProvider());
		cbtViewer.setInput(new ArrayList<String>());

		cbtViewer.addCheckStateListener(event -> {
			System.out
				.println("CDTFoldingPreferenceBlock.createControl(...).new ICheckStateListener() {...}.checkStateChanged()");

			if (event.getChecked()) {
				regexes.add((String) event.getElement());
			} else {
				regexes.remove((String) event.getElement());
			}

			updateCurrentRegex();
		});

		return cbtViewer;
	}

	private Button addButton(Composite editButtons, Text t) {
		Button addButton = ControlFactory.createPushButton(editButtons, "Add...");

		addButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				System.out
						.println("CDTFoldingPreferenceBlock.createControl(...).new MouseListener() {...}.mouseUp()");

				@SuppressWarnings("unchecked")
				ArrayList<String> input = (ArrayList<String>) cbtViewer
						.getInput();
				
				try {
					Pattern.compile(t.getText());
					errorLabel.setVisible(false);
					input.add(t.getText());
					cbtViewer.setInput(input);
					updateCurrentRegex();
				} catch (PatternSyntaxException psyex) {
					errorLabel.setVisible(true);
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				System.out
						.println("CDTFoldingPreferenceBlock.createControl(...).new MouseListener() {...}.mouseDown()");
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				System.out
						.println("CDTFoldingPreferenceBlock.createControl(...).new MouseListener() {...}.mouseDoubleClick()");
			}
		});
		
		return addButton;
	}
	
	private Button removeButton(Composite editButtons) {
		Button removeButton = ControlFactory.createPushButton(editButtons,
				"Remove...");

		removeButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				System.out
						.println("CDTFoldingPreferenceBlock.createControl(...).new MouseListener() {...}.mouseUp()");

				String selection = cbtViewer.getSelection().toString();
				selection = selection.substring(1, selection.length() - 1);

				@SuppressWarnings("unchecked")
				ArrayList<String> input = (ArrayList<String>) cbtViewer.getInput();

				input.remove(selection);
				
				if (cbtViewer.getChecked(selection)) {
					regexes.remove(selection);
					updateCurrentRegex();
				}

				cbtViewer.setInput(input);
				cbtViewer.refresh();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				System.out
						.println("CDTFoldingPreferenceBlock.createControl(...).new MouseListener() {...}.mouseDown()");
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				System.out
						.println("CDTFoldingPreferenceBlock.createControl(...).new MouseListener() {...}.mouseDoubleClick()");
			}
		});
		
		return removeButton;
	}
	
	private Button moveButton (Composite parent, Move move) {
		String label;
		
		switch (move) {
			case UP: label = "Move up..."; break;
			case DOWN: label = "Move down..."; break;
			default: label = "Move..."; break;
		}
		
		Button moveUpButton = ControlFactory.createPushButton(parent, label);
		
		moveUpButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				System.out
						.println("CDTFoldingPreferenceBlock.moveUpButton(...).new MouseListener() {...}.mouseUp()");
				
				String selection = cbtViewer.getSelection().toString();
				selection = selection.substring(1, selection.length() - 1);

				@SuppressWarnings("unchecked")
				ArrayList<String> input = (ArrayList<String>) cbtViewer.getInput();
				
				int idx = -1;
				
				for (int i = 0; i < input.size(); i++) {
					if (input.get(i).equals(selection)) idx = i;
				}
				
				if (idx != -1) {
					input.remove(selection);
					
					switch (move) {
						case UP: input.add(idx > 0 ? idx - 1 : idx, selection); break;
						case DOWN: input.add(idx < input.size() ? idx + 1 : idx, selection); break;
						default: input.add(idx, selection); break;
					}	
				}
				
				cbtViewer.setInput(input);
				
				updateCurrentRegex();
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				System.out
						.println("CDTFoldingPreferenceBlock.moveUpButton(...).new MouseListener() {...}.mouseDown()");
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				System.out
						.println("CDTFoldingPreferenceBlock.moveUpButton(...).new MouseListener() {...}.mouseDoubleClick()");
			}
		});
		
		return moveUpButton;
	}

	private Text addTextField(Composite composite) {
		Text t = ControlFactory.createTextField(composite);

		t.setSize(500, 80);
		t.addModifyListener(e -> {			
			try {
				Pattern.compile(t.getText());
				errorLabel.setVisible(false);
				t.setBackground(new Color(null, new RGB(255, 255, 255)));
				childPreferenceStore.setValue(CDTFoldingConstants.TF_REGEX_KEY_STR, t.getText());
			} catch (PatternSyntaxException psyex) {
				errorLabel.setVisible(true);
				t.setBackground(new Color(null, new RGB(254, 0, 0)));
			}
		});

		return t;
	}
	
	private void addErrorLabel(Composite parent) {
		errorLabel = ControlFactory.createBoldLabel(parent, CDTFoldingConstants.ERROR_REGEX);
		errorLabel.setForeground(new Color(null, new RGB(254, 0, 0)));
		errorLabel.setVisible(false);
	}
	
	private void currentRegexLabel(Composite parent) {
		currentRegexLabel = ControlFactory.createLabel(parent, CDTFoldingConstants.ERROR_REGEX);
	}
	
	@SuppressWarnings("unchecked")
	private void updateCurrentRegex() {
		regexes.stream().forEachOrdered(e -> System.out.println(e + " "));
		
		Collections.sort(regexes, (m, n) -> {
			ArrayList<String> input = (ArrayList<String>) cbtViewer.getInput();

			for (String i : input) {
				if (i.equals(m)) {
					return -1;
				} else if (i.equals(n)) {
					return 1;

				}
			}
			return 0;
		});
		
		if (regexes.size() > 0) {
			currentRegexLabel.setVisible(true);
			currentRegexLabel.setText(CDTFoldingConstants.CURRENT_SELECTED_REGEX + regexes.get(0));
		} else {
			currentRegexLabel.setVisible(false);
		}
	}

	@Override
	public void initialize() {
		System.out.println("CDTFoldingPreferenceBlock.initialize()");

		childPreferenceStore.addKeys(foldingKeys);
		
		restoreCBTViewer();
	}
	
	@SuppressWarnings("serial")
	private void restoreCBTViewer() {
		String[] regexes = store.getString(CDTFoldingConstants.ENTIRE_INPUT)
				.split(CDTFoldingConstants.REGEX_SEPARATOR);
		
		System.out.println(Arrays.toString(regexes));

		cbtViewer.setInput(new ArrayList<String>() {
			{
				for (int i = 0; i < regexes.length; i++) {
					add(regexes[i]);
				}
			}
		});
		
		updateCurrentRegex();
	}

	@Override
	public void performOk() {
		System.out.println("CDTFoldingPreferenceBlock.performOk()");

		serializeList();
		childPreferenceStore.propagate();
	}
	
	@SuppressWarnings("unchecked")
	private void serializeList() {
		StringBuilder sb = new StringBuilder();
		
		for (String regex : regexes) {
			sb.append(regex);
			sb.append(CDTFoldingConstants.REGEX_SEPARATOR);
		}
		
		store.setValue(CDTFoldingConstants.TF_REGEX_KEY_STR, sb.toString());
		
		sb.delete(0, sb.length());
		
		for (String regex : (ArrayList<String>) cbtViewer.getInput()) {
			sb.append(regex); sb.append(CDTFoldingConstants.REGEX_SEPARATOR);
		}
		
		store.setValue(CDTFoldingConstants.ENTIRE_INPUT, sb.toString());
	}

	@Override
	public void performDefaults() {
		System.out.println("CDTFoldingPreferenceBlock.performDefaults()");

		childPreferenceStore.loadDefaults();
	}

	@Override
	public void dispose() {
		System.out.println("CDTFoldingPreferenceBlock.dispose()");
		childPreferenceStore.stop();
	}
}