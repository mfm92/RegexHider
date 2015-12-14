/*******************************************************************************
 * Copyright (c) 2005, 2010 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - initial API and implementation
 *******************************************************************************/
package org.softwarepraktikum.plugin.cdtregexdebugger;

import org.eclipse.cdt.launch.internal.LocalCDILaunchDelegate;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

@SuppressWarnings({"restriction"})
public class FoldingLaunchConfigType implements ILaunchConfigurationDelegate {

	LocalCDILaunchDelegate localLaunchDelegate = new LocalCDILaunchDelegate();

	public void launch (ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		
		localLaunchDelegate.launch(configuration, mode, launch, monitor);
	}
}
