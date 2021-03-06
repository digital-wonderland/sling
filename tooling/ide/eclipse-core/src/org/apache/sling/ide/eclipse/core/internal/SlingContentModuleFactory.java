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
package org.apache.sling.ide.eclipse.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.ide.eclipse.core.ProjectUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ModuleFile;
import org.eclipse.wst.server.core.util.ModuleFolder;
import org.eclipse.wst.server.core.util.ProjectModule;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;

public class SlingContentModuleFactory extends ProjectModuleFactoryDelegate {

    static final String SLING_CONTENT_FACET_ID = "sling.content";
	private static final IPath[] SETTINGS_PATHS = new IPath[] {new Path(".settings")};
    
	@Override
	public IModule[] getModules(IProject project) {
	    final IModule[] result = super.getModules(project);
	    if (result!=null && result.length>0) {
	        return result;
	    } else {
	        // try clearing the cache
	        // might fix SLING-3663 which could be due to a synchronization issue at first access time
	        clearCache(project);
	        return super.getModules(project);
	    }
	}
	
    @Override
    protected IPath[] getListenerPaths() {
    	// returning the .settings path instead of null (as done by the parent)
    	// results in clearing the cache on changes to .settings - which in turn
    	// results in re-evaluating facet changes.
    	// we could be more specific here but .settings changes are infrequent anyway.
    	return SETTINGS_PATHS;
    }

    @Override
    public ModuleDelegate getModuleDelegate(IModule module) {

        return new SlingContentModuleDelegate(module);
    }

    @Override
    protected IModule createModule(IProject project) {

        try {
            IFacetedProject facetedProject = ProjectFacetsManager.create(project);
            if (facetedProject == null) {
                return null;
            }
            for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
                if (facet.getProjectFacet().getId().equals(SLING_CONTENT_FACET_ID)) {
                    return createModule(project.getName(), project.getName(), SLING_CONTENT_FACET_ID, "1.0", project);
                }
            }
        } catch (CoreException ce) {
            // TODO logging
        }

        return null;
    }

    static class SlingContentModuleDelegate extends ProjectModule {


        public SlingContentModuleDelegate(IModule module) {
            super(module.getProject());
        }

        @Override
        public IStatus validate() {
            return Status.OK_STATUS; // TODO actually validate
        }

        @Override
        public IModuleResource[] members() throws CoreException {
            final List<IModuleResource> resources = new ArrayList<IModuleResource>();
            final IFolder syncFolder = getProject().getFolder(ProjectUtil.getSyncDirectoryValue(getProject()));

            if (!syncFolder.exists()) {
                return new IModuleResource[0];
            }

            getProject().accept(new IResourceVisitor() {
                @Override
                public boolean visit(IResource resource) throws CoreException {

                    if (resource.getType() == IResource.PROJECT) {
                        return true;
                    }

                    IPath relativePath = resource.getProjectRelativePath();

                    if (relativePath.isPrefixOf(syncFolder.getProjectRelativePath())) {
                        // parent directory of our sync location, don't process but recurse
                        return true;
                    }

                    // unrelated resource tree, stop processing
                    if (!syncFolder.getProjectRelativePath().isPrefixOf(relativePath)) {
                        return false;
                    }

                    IPath modulePath = relativePath.removeFirstSegments(syncFolder.getProjectRelativePath()
                            .segmentCount()); // remove sync dir

                    IModuleResource moduleFile = null;

                    if (resource.getType() == IResource.FILE) {
                        moduleFile = new ModuleFile((IFile) resource, resource.getName(), modulePath);
                    } else if (resource.getType() == IResource.FOLDER) {
                        moduleFile = new ModuleFolder((IFolder) resource, resource.getName(), modulePath);
                    }

                    if (moduleFile != null)
                        resources.add(moduleFile);

                    return true;
                }
            });

            return resources.toArray(new IModuleResource[resources.size()]);
        }

        @Override
        public IModule[] getChildModules() {
            return new IModule[0]; // TODO revisit, do we need child modules?
        }
    }
}
