/*******************************************************************************
 * Copyright (c) 2004, 2011 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package cosy.sbg.softwarepraktikum.plugin.cdtregexdebugger;

import java.io.File;

import org.eclipse.cdt.core.IBinaryParser.IBinaryObject;
import org.eclipse.cdt.debug.core.ICDIDebugger2;
import org.eclipse.cdt.debug.core.cdi.ICDISession;
import org.eclipse.cdt.debug.mi.core.GDBCDIDebugger2;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
 
/**
 * Implementing the cdebugger extension point for basic launch configurations.
 */
public class CDTFoldingDebugger implements ICDIDebugger2 {
	
	GDBCDIDebugger2 gdbCdiDebugger2 = new GDBCDIDebugger2();

	@Override
	public ICDISession createDebuggerSession (ILaunch launch, IBinaryObject exe, IProgressMonitor monitor)
			throws CoreException {
		System.out.println("CDTFoldingDebugger.createDebuggerSession()");
		return gdbCdiDebugger2.createDebuggerSession(launch, exe, monitor);
	}

	@Override
	public ICDISession createSession (ILaunch launch, File executable, IProgressMonitor monitor)
			throws CoreException {
		
		System.out.println("CDTFoldingDebugger.createSession()");
		return gdbCdiDebugger2.createSession(launch, executable, monitor);
	}

}