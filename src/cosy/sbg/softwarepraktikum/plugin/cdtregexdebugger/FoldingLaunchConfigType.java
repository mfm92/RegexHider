/*******************************************************************************
 * Copyright (c) 2004, 2015 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * QNX Software Systems - Initial API and implementation
 * Anton Leherbauer (Wind River Systems) - bugs 205108, 212632, 224187
 * Ken Ryall (Nokia) - bug 188116
 * Marc Khouzam (Ericsson) - Modernize Run launch (bug 464636)
 *******************************************************************************/
package cosy.sbg.softwarepraktikum.plugin.cdtregexdebugger;

import org.eclipse.cdt.launch.internal.LocalRunLaunchDelegate;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
 
/**
 * The launch configuration delegate for the CDI debugger session types.
 */
@SuppressWarnings("restriction")
public class FoldingLaunchConfigType extends LaunchConfigurationDelegate {

	LocalRunLaunchDelegate localLaunchDelegate = new LocalRunLaunchDelegate();

	public void launch (ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		
		localLaunchDelegate.launch(configuration, mode, launch, monitor);
	}

}