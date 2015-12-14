package org.softwarepraktikum.plugin.cdtfolding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.cdt.ui.text.folding.ICFoldingPreferenceBlock;
import org.eclipse.cdt.utils.ui.controls.ControlFactory;
import org.eclipse.jface.preference.ColorSelector;
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
import org.eclipse.swt.widgets.Combo;
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

	CDTFoldingChildPreferenceStore childPreferenceStore;

	CheckboxTableViewer cbtViewer;

	HashSet<String> regexes = new HashSet<>();

	CDTFoldingChildPreferenceStore.FoldingKey[] foldingKeys;
	
	Label errorLabel;
	Label currentRegexLabel;
	
	ColorSelector bgColorSelector;
	ColorSelector fgColorSelector;
	
	Label bgLabel;
	Label fgLabel;

	public CDTFoldingPreferenceBlock() {
		System.out.println("CDTFoldingPreferenceBlock.CDTFoldingPreferenceBlock()");

		store = CDTFolderPlugin.getDefault().getPreferenceStore();
		fillOverlayMap();
		childPreferenceStore = new CDTFoldingChildPreferenceStore(store, foldingKeys);
	}

	private void fillOverlayMap () {
		ArrayList<CDTFoldingChildPreferenceStore.FoldingKey> foldingKeys = new ArrayList<>();

		foldingKeys.add(new CDTFoldingChildPreferenceStore.FoldingKey(
				CDTFoldingChildPreferenceStore.TD_STRING, CDTFoldingConstants.CHECKED_STRING_INPUT));

		foldingKeys.add(new CDTFoldingChildPreferenceStore.FoldingKey(
				CDTFoldingChildPreferenceStore.TD_STRING, CDTFoldingConstants.ENTIRE_INPUT));
		
		foldingKeys.add(new CDTFoldingChildPreferenceStore.FoldingKey(
				CDTFoldingChildPreferenceStore.TD_STRING, CDTFoldingConstants.COMBO_CHOICE));
		
		foldingKeys.add(new CDTFoldingChildPreferenceStore.FoldingKey(
				CDTFoldingChildPreferenceStore.TD_STRING, CDTFoldingConstants.COLOR_PICKED_BG));
		
		foldingKeys.add(new CDTFoldingChildPreferenceStore.FoldingKey(
				CDTFoldingChildPreferenceStore.TD_STRING, CDTFoldingConstants.COLOR_PICKED_FG));
		
		this.foldingKeys = foldingKeys.toArray(new CDTFoldingChildPreferenceStore.FoldingKey[foldingKeys
				.size()]);
	}

	@Override
	public Control createControl (Composite parent) {
		System.out.println("CDTFoldingPreferenceBlock.createControl()");

		childPreferenceStore.load();
		childPreferenceStore.start();

		Composite inner = new Composite(parent, SWT.NONE);
		setLayout(inner);

		Composite group = ControlFactory.createGroup(inner, CDTFoldingConstants.TF_TEXTFIELD_NAME, 1);

		Text t = addTextField(group);

		ControlFactory.createEmptySpace(inner);

		cbtViewer = addCBTViewer(inner);

		Composite editButtons = ControlFactory.createComposite(group, 4);

		addButton(editButtons, t);
		removeButton(editButtons);

		moveButton(editButtons, Move.UP);
		moveButton(editButtons, Move.DOWN);
		
		highlightOrFoldCheckBox(inner);
		
		Composite colorComposite = ControlFactory.createComposite(inner, 2);
		colorScheme(colorComposite);
		
		addErrorLabel(inner);

		return inner;
	}

	private void setLayout (Composite parent) {
		GridLayout layout = new GridLayout(1, true);

		layout.verticalSpacing = 3;
		layout.marginWidth = 0;
		parent.setLayout(layout);
	}

	private CheckboxTableViewer addCBTViewer (Composite parent) {
		CheckboxTableViewer cbtViewer = ControlFactory.createListViewer(parent, new String[] {}, 420, 300,
				SWT.V_SCROLL);
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
		});

		return cbtViewer;
	}

	private Button addButton (Composite editButtons, Text t) {
		Button addButton = ControlFactory.createPushButton(editButtons, "Add...");

		addButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp (MouseEvent e) {
				System.out.println("CDTFoldingPreferenceBlock.createControl(...).new MouseListener() {...}.mouseUp()");

				@SuppressWarnings("unchecked")
				ArrayList<String> input = (ArrayList<String>) cbtViewer.getInput();

				try {
					Pattern.compile(t.getText());
					
					errorLabel.setVisible(false);
					input.add(t.getText());
					cbtViewer.setInput(input);
				} catch (PatternSyntaxException psyex) {
					errorLabel.setVisible(true);
				}
			}

			@Override
			public void mouseDown (MouseEvent e) {
				System.out
						.println("CDTFoldingPreferenceBlock.createControl(...).new MouseListener() {...}.mouseDown()");
			}

			@Override
			public void mouseDoubleClick (MouseEvent e) {
				System.out
						.println("CDTFoldingPreferenceBlock.createControl(...).new MouseListener() {...}.mouseDoubleClick()");
			}
		});

		return addButton;
	}

	private Button removeButton (Composite editButtons) {
		Button removeButton = ControlFactory.createPushButton(editButtons, "Remove...");

		removeButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp (MouseEvent e) {
				System.out.println("CDTFoldingPreferenceBlock.createControl(...).new MouseListener() {...}.mouseUp()");

				String selection = cbtViewer.getSelection().toString();
				selection = selection.substring(1, selection.length() - 1);

				@SuppressWarnings("unchecked")
				ArrayList<String> input = (ArrayList<String>) cbtViewer.getInput();

				input.remove(selection);

				if (cbtViewer.getChecked(selection)) {
					regexes.remove(selection);
				}

				cbtViewer.setInput(input);
				cbtViewer.refresh();
			}

			@Override
			public void mouseDown (MouseEvent e) {
				System.out.println("CDTFoldingPreferenceBlock.createControl(...).new MouseListener() {...}.mouseDown()");
			}

			@Override
			public void mouseDoubleClick (MouseEvent e) {
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
			public void mouseUp (MouseEvent e) {
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
						case UP:
							input.add(idx > 0 ? idx - 1 : idx, selection);
							break;
						case DOWN:
							input.add(idx < input.size() ? idx + 1 : idx, selection);
							break;
						default:
							input.add(idx, selection);
							break;
					}
				}

				cbtViewer.setInput(input);
			}

			@Override
			public void mouseDown (MouseEvent e) {
				System.out.println("CDTFoldingPreferenceBlock.moveUpButton(...).new MouseListener() {...}.mouseDown()");
			}

			@Override
			public void mouseDoubleClick (MouseEvent e) {
				System.out.println("CDTFoldingPreferenceBlock.moveUpButton(...).new MouseListener() {...}.mouseDoubleClick()");
			}
		});

		return moveUpButton;
	}

	private Text addTextField (Composite composite) {
		Text t = ControlFactory.createTextField(composite);

		Color bgColor = t.getBackground();
		Color errorColor = new Color(null, new RGB(253, 0, 0));
		
		t.setSize(400, 80);
		
		/*
		 * Check if the current user-specified regular expression is
		 * actually valid. If not, turn the background of the text
		 * field red. Also there's a hidden label that says "Invalid
		 * Regular Expression!", that one will be turned visible too.
		 */
		t.addModifyListener(e -> {
			try {
				// Try to compile the regex. If it's invalid a PatternSyntaxException will be thrown.
				Pattern.compile(t.getText());
				errorLabel.setVisible(false);
				t.setBackground(bgColor);
				
				// Store regex in the preference store.
				childPreferenceStore.setValue(CDTFoldingConstants.CHECKED_STRING_INPUT, t.getText());
			} catch (PatternSyntaxException psyex) {
				// If all is well, set error label invisible
				errorLabel.setVisible(true);
				
				// Also, restore the background color of the text field
				t.setBackground(errorColor);
			}
		});

		return t;
	}

	private void addErrorLabel (Composite parent) {
		errorLabel = ControlFactory.createBoldLabel(parent, CDTFoldingConstants.ERROR_REGEX);
		errorLabel.setForeground(new Color(null, new RGB(254, 0, 0)));
		errorLabel.setVisible(false);
		errorLabel.setAlignment(SWT.RIGHT);
	}
	
	private void highlightOrFoldCheckBox (Composite parent) {
		String[] choices = new String[]{
			CDTFoldingConstants.COMBO_CHOICE_FOLD, CDTFoldingConstants.COMBO_CHOICE_HIGHLIGHT
		};
		
		String defaultSelected = choices[0];
		
		Combo combo = ControlFactory.createSelectCombo(parent, choices, defaultSelected);
		
		combo.setText(store.getString(CDTFoldingConstants.COMBO_CHOICE));
		
		combo.addModifyListener(modifyEvent -> {
			System.out.println("CDTFoldingPreferenceBlock.highlightOrFoldCheckBox()");
			store.setValue(CDTFoldingConstants.COMBO_CHOICE, combo.getText());
			turnOnOffColors();
		});
	}
	
	private void colorScheme (Composite parent) {
		bgLabel = ControlFactory.createLabel(parent, CDTFoldingConstants.SELECT_BG);
		bgColorSelector = new ColorSelector(parent);
		
		fgLabel = ControlFactory.createLabel(parent, CDTFoldingConstants.SELECT_FG);
		fgColorSelector = new ColorSelector(parent);
		
		bgColorSelector.setColorValue(CDTUtilities.restoreRGB(store.getString(CDTFoldingConstants.COLOR_PICKED_BG)));
		fgColorSelector.setColorValue(CDTUtilities.restoreRGB(store.getString(CDTFoldingConstants.COLOR_PICKED_FG)));
		
		bgColorSelector.addListener(event -> {
			RGB currentRGB = bgColorSelector.getColorValue();
			
			StringBuilder sb = new StringBuilder();
			sb.append(Integer.toString(currentRGB.red) + " ");
			sb.append(Integer.toString(currentRGB.green) + " ");
			sb.append(Integer.toString(currentRGB.blue));
			
			store.setValue(CDTFoldingConstants.COLOR_PICKED_BG, sb.toString());
		});
		
		fgColorSelector.addListener(event -> {
			RGB currentRGB = fgColorSelector.getColorValue();
			
			StringBuilder sb = new StringBuilder();
			sb.append(Integer.toString(currentRGB.red) + " ");
			sb.append(Integer.toString(currentRGB.green) + " ");
			sb.append(Integer.toString(currentRGB.blue));
			
			store.setValue(CDTFoldingConstants.COLOR_PICKED_FG, sb.toString());
		});
		
		turnOnOffColors();
	}
	
	private void turnOnOffColors() {
		if (store.getString(CDTFoldingConstants.COMBO_CHOICE).equals(CDTFoldingConstants.COMBO_CHOICE_HIGHLIGHT)) {
			fgLabel.setVisible(true);
			bgLabel.setVisible(true);
			bgColorSelector.setEnabled(true);
			fgColorSelector.setEnabled(true);
		} else {
			fgLabel.setVisible(false);
			bgLabel.setVisible(false);
			bgColorSelector.setEnabled(false);
			fgColorSelector.setEnabled(false);
		}
	}

	@Override
	public void initialize () {
		System.out.println("CDTFoldingPreferenceBlock.initialize()");

		childPreferenceStore.addKeys(foldingKeys);

		restoreCBTViewer();
	}

	private boolean containedIn(String[] arr, String v) {
		for (String s : arr) {
			if (s.equals(v)) return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("serial")
	private void restoreCBTViewer () {
		System.out.println("CDTFoldingPreferenceBlock.restoreCBTViewer()");
		
		System.out.format("Checked string - restore: %s\n", store.getString(CDTFoldingConstants.CHECKED_STRING_INPUT));
		System.out.format("Entire string - restore: %s\n", store.getString(CDTFoldingConstants.ENTIRE_INPUT));
		
		// All checked regexes that the user entered in the previous Eclipse instance, checked or not
		// or-ed together at this point
		String checkedString = store.getString(CDTFoldingConstants.CHECKED_STRING_INPUT);
		
		// All regexes that the user entered in the previous Eclipse instance, checked or not
		// or-ed together at this point
		String entireString = store.getString(CDTFoldingConstants.ENTIRE_INPUT);
		
		String[] checkedRegexes;
		String[] regexes;

		// Split checked regex string into the single regexes it's made of...
		if (checkedString.length() > 1) {
			checkedString = checkedString.substring(1, checkedString.length() - 1);
			checkedRegexes = checkedString.split(Pattern.quote(")|("));
		} else {
			checkedRegexes = new String[]{};
		}
		
		if (entireString.length() > 1) {
			entireString = entireString.substring(1, entireString.length() - 1);
			regexes = entireString.split(Pattern.quote(")|("));
		} else {
			regexes = new String[]{};
		}
		
		System.out.format("Checked trimmed: %s\n", checkedString);
		System.out.format("Entire trimmed: %s\n", entireString);

		System.out.println(Arrays.toString(checkedRegexes));
		System.out.println(Arrays.toString(regexes));

		// Add regexes to the view, check all regexes that were checked previously
		cbtViewer.setInput(new ArrayList<String>() {
			{
				for (int i = 0; i < regexes.length; i++) {
					add(regexes[i]);
					cbtViewer.setChecked(regexes[i], containedIn(checkedRegexes, regexes[i]));
				}
			}
		});
		
		for (String s : checkedRegexes) {
			this.regexes.add(s);
		}
	}

	@Override
	public void performOk () {
		System.out.println("CDTFoldingPreferenceBlock.performOk()");

		orRegexes();

		childPreferenceStore.propagate();
	}

	@SuppressWarnings("unchecked")
	private void orRegexes () {		
		if (regexes.isEmpty()) {
			store.setValue(CDTFoldingConstants.CHECKED_STRING_INPUT, "");
			store.setValue(CDTFoldingConstants.ENTIRE_INPUT, "");
			return;
		}
		
		StringBuilder sb = new StringBuilder();

		// Concatenate all regexes together that the user checked too
		for (String regex : regexes) {
			sb.append("(");
			sb.append(regex);
			sb.append(")|");
		}
		
		// get rid of the trailing bar
		sb.delete(sb.toString().length() - 1, sb.length());

		// save or-ed regex in the preference store
		// note that we'll access this in CDTFolderHighlighter.apply(ITextEditor, ProjectionViewer)!
		store.setValue(CDTFoldingConstants.CHECKED_STRING_INPUT, sb.toString());

		sb.delete(0, sb.length());

		/*
		 * Save non-checked regexes aswell, such that the user doesn't have to enter
		 * these again when opening another instance of Eclipse.
		 */
		for (String regex : (ArrayList<String>) cbtViewer.getInput()) {
			sb.append("(");
			sb.append(regex);
			sb.append(")|");
		}

		sb.delete(sb.toString().length() - 1, sb.length());
		
		// store in preference store
		store.setValue(CDTFoldingConstants.ENTIRE_INPUT, sb.toString());
		
		System.out.println("ENTIRE INPUT: " + store.getString(CDTFoldingConstants.ENTIRE_INPUT));
		System.out.println("CHECKED REGEXES: " + store.getString(CDTFoldingConstants.CHECKED_STRING_INPUT));
	}

	@Override
	public void performDefaults () {
		System.out.println("CDTFoldingPreferenceBlock.performDefaults()");

		childPreferenceStore.loadDefaults();
	}

	@Override
	public void dispose () {
		System.out.println("CDTFoldingPreferenceBlock.dispose()");
		childPreferenceStore.stop();
	}
	
	@SuppressWarnings("unused")
	private void repair() {
		store.setValue(CDTFoldingConstants.CHECKED_STRING_INPUT, "");
		store.setValue(CDTFoldingConstants.ENTIRE_INPUT, "");
	}
}