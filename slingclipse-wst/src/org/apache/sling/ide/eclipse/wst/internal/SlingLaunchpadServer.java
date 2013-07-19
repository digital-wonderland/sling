/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.ide.eclipse.wst.internal;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.FacetUtil;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ServerDelegate;

public class SlingLaunchpadServer extends ServerDelegate {

    private static final String MODULE_TYPE_SLING_CONTENT = "sling.content";
    private SlingLaunchpadConfiguration config;

    public SlingLaunchpadConfiguration getConfiguration() {

        if (config != null) {
            return config;
        }

        IFolder folder = getServer().getServerConfiguration();

        config = new SlingLaunchpadConfiguration(folder);
        config.load(new NullProgressMonitor()); // TODO progress monitor

        return config;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.server.core.model.ServerDelegate#canModifyModules(org.eclipse.wst.server.core.IModule[],
     * org.eclipse.wst.server.core.IModule[])
     */
    @Override
    public IStatus canModifyModules(IModule[] toAdd, IModule[] toRemove) {

        System.out.println("SlingLaunchpadServer.canModifyModules()");

        if (toAdd == null) {
            return Status.OK_STATUS;
        }

        for (IModule module : toAdd) {

            if (!MODULE_TYPE_SLING_CONTENT.equals(module.getModuleType().getId())) {
                return new Status(IStatus.ERROR, "org.apache.sling.slingclipse", 0,
                        "Will only handle modules of type 'sling.content'", null);
            }
        }

        return Status.OK_STATUS;
    }

    @Override
    public IModule[] getChildModules(IModule[] module) {

        System.out.println("SlingLaunchpadServer.getChildModules()");

        if (module == null) {
            return null;
        }

        // no nested modules for now
        return new IModule[0];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.server.core.model.ServerDelegate#getRootModules(org.eclipse.wst.server.core.IModule)
     */
    @Override
    public IModule[] getRootModules(IModule arg0) throws CoreException {

        System.out.println("SlingLaunchpadServer.getRootModules()");

        if (MODULE_TYPE_SLING_CONTENT.equals(arg0.getModuleType().getId())) {
            return new IModule[] { arg0 };
        }

        throw new CoreException(new Status(IStatus.ERROR, "org.apache.sling.slingclipse", 0,
                "Will only handle modules of type 'sling.content'", null));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.server.core.model.ServerDelegate#modifyModules(org.eclipse.wst.server.core.IModule[],
     * org.eclipse.wst.server.core.IModule[], org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void modifyModules(IModule[] toAdd, IModule[] toRemove, IProgressMonitor arg2) throws CoreException {

        System.out.println("SlingLaunchpadServer.modifyModules()");

        IStatus status = canModifyModules(toAdd, toRemove);
        if (!status.isOK()) {
            throw new CoreException(status);
        }

        for (IModule module : toAdd) {
            System.out.println("Adding module " + module);
        }

        for (IModule module : toRemove) {
            System.out.println("Removing module " + module);
        }
    }

}