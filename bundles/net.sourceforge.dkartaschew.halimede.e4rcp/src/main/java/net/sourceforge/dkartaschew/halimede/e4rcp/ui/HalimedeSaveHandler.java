package net.sourceforge.dkartaschew.halimede.e4rcp.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.internal.workbench.PartServiceSaveHandler;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.e4rcp.dialogs.ListSelectionDialog;

@SuppressWarnings("restriction")
public class HalimedeSaveHandler extends PartServiceSaveHandler {

	private static class SaveHandlerLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			return ((MPart) element).getLocalizedLabel();
		}
	};
	
	@Inject
	private Shell shell;

	@Override
	public Save promptToSave(MPart dirtyPart) {
		String desc = dirtyPart.getLabel();
		MessageDialog dialog = new MessageDialog(shell, //
				"Save Material", //
				PluginDefaults.getResourceManager().createImage(PluginDefaults.createImageDescriptor(//
						PluginDefaults.IMG_APPLICATION)), //
				"Save Self-Signed Certificate '" + desc + "'?", //
				MessageDialog.QUESTION, new String[] { "Don't Save", "Cancel", "Save" }, 2);
		int result = dialog.open();
		switch (result) {
		case 0:
			return Save.NO;
		case 1:
			return Save.CANCEL;
		case 2:
			return Save.YES;
		}
		return super.promptToSave(dirtyPart);
	}

	@Override
	public Save[] promptToSave(Collection<MPart> dirtyParts) {
		if (dirtyParts.size() == 1) {
			return new Save[] { promptToSave(dirtyParts.iterator().next()) };
		}
		LabelProvider labelProvider = new SaveHandlerLabelProvider();
		List<MPart> parts = new ArrayList<>(dirtyParts);
		ListSelectionDialog dialog = new ListSelectionDialog(shell, parts,
				ArrayContentProvider.getInstance(), labelProvider,
				"Save Material", "Save Self-Signed Certificates");
		dialog.setInitialSelections(parts.toArray());
		if (dialog.open() == IDialogConstants.CANCEL_ID) {
			return new Save[] { Save.CANCEL };
		}

		Object[] toSave = dialog.getResult();
		Save[] retSaves = new Save[parts.size()];
		Arrays.fill(retSaves, Save.NO);
		for (int i = 0; i < retSaves.length; i++) {
			MPart part = parts.get(i);
			for (Object o : toSave) {
				if (o == part) {
					retSaves[i] = Save.YES;
					break;
				}
			}
		}
		return retSaves;
	}

}
