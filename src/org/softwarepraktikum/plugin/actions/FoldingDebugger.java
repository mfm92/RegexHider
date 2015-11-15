package org.softwarepraktikum.plugin.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class FoldingDebugger implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public FoldingDebugger() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		String regex = "\\bin[\\n]?t\\b";
		String content = getCurrentEditorContent();

		action.addPropertyChangeListener(event -> System.out.println("Action Property Change!"));
		
		Map<Integer, Integer> newLineMap = preProcess(content);

		StringBuilder display = new StringBuilder(content);

		for (Map.Entry<Integer, Integer> match : getMatchingLines(regex,
				content, newLineMap).entrySet()) {
			display.append(String.format("IDX from %d to %d, Lines %d to %d\n",
					match.getKey(), match.getValue(),
					getLineNr(match.getKey(), newLineMap),
					getLineNr(match.getValue(), newLineMap)));
		}

		MessageDialog.openInformation(
				window.getShell(),
				"Plugin",
				"Hello, Eclipse world, your text is: "
						+ display.toString());

		System.out.println(display.toString());
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	private Map<Integer, Integer> preProcess(String content) {
		Map<Integer, Integer> newLinePrefix = new HashMap<>();

		int counter = 0;

		for (int idx = 0; idx < content.length(); idx++) {
			if (content.charAt(idx) == '\n') {
				newLinePrefix.put(counter++, idx);
			}
		}

		return newLinePrefix;
	}

	private String getCurrentEditorContent() {
		final IEditorPart editor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (!(editor instanceof ITextEditor))
			return null;
		ITextEditor ite = (ITextEditor) editor;
		IDocument doc = ite.getDocumentProvider().getDocument(
				ite.getEditorInput());
		return doc.get();
	}

	private Map<Integer, Integer> getMatchingLines(String regex,
			String content, Map<Integer, Integer> newLineMap) {
		Pattern regexPattern = Pattern.compile(regex);

		Matcher regexMatcher = regexPattern.matcher(content);

		Map<Integer, Integer> matchList = new HashMap<>();

		while (regexMatcher.find()) {
			matchList.put(regexMatcher.start(), regexMatcher.end());
		}

		return matchList;
	}

	private int getLineNr(int idx, Map<Integer, Integer> newLineMap) {
		
		int b = 0;
		int e = newLineMap.size();
		
		while (b <= e) {
			int mid = (b+e) / 2;
			
			if (newLineMap.get(mid) <= idx && newLineMap.get(mid + 1) >= idx) {
				return mid + 2;
			} else if (newLineMap.get(mid) > idx) {
				e = mid - 1;
			} else {
				b = mid + 1;
			}
		}
		
		return -1;
	}
}