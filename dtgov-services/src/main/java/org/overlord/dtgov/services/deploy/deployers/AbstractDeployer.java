/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.dtgov.services.deploy.deployers;

import java.util.Map;

import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactEnum;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.ExtendedArtifactType;
import org.overlord.dtgov.common.Target;
import org.overlord.dtgov.common.model.DtgovModel;
import org.overlord.dtgov.services.deploy.Deployer;
import org.overlord.dtgov.services.i18n.Messages;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.common.SrampModelUtils;


/**
 * Abstract class that contains the main methods and attributes that a deployer
 * can use.
 *
 * @author David Virgil Naranjo
 */
public abstract class AbstractDeployer<T extends Target> implements Deployer<T> {


    /**
     * Instantiates a new abstract deployer.
     */
    public AbstractDeployer() {
    }

    /**
     * Records undeployment information for the given artifact. The undeployment
     * information is saved as another artifact in the repository with a
     * relationship back to the deployment artifact.
     *
     * @param artifact
     *            the artifact
     * @param target
     *            the target
     * @param props
     *            the props
     * @param client
     *            the client
     * @throws SrampClientException
     *             the sramp client exception
     * @throws SrampAtomException
     *             the sramp atom exception
     */
    protected void recordUndeploymentInfo(BaseArtifactType artifact, T target,
            Map<String, String> props, SrampAtomApiClient client) throws SrampClientException,
            SrampAtomException {
        ExtendedArtifactType undeploymentArtifact = new ExtendedArtifactType();
        undeploymentArtifact.setArtifactType(BaseArtifactEnum.EXTENDED_ARTIFACT_TYPE);
        undeploymentArtifact.setExtendedType(DtgovModel.UndeploymentInformationType);
        undeploymentArtifact.setName(artifact.getName() + ".undeploy"); //$NON-NLS-1$
        undeploymentArtifact.setDescription(Messages.i18n.format(
                "DeploymentResource.UndeploymentInfoDescription", artifact.getName())); //$NON-NLS-1$
        SrampModelUtils.setCustomProperty(undeploymentArtifact, DtgovModel.CUSTOM_PROPERTY_DEPLOY_TARGET, target.getName());
        SrampModelUtils.setCustomProperty(undeploymentArtifact, DtgovModel.CUSTOM_PROPERTY_DEPLOY_TYPE, target.getType().name());
        SrampModelUtils.setCustomProperty(undeploymentArtifact, DtgovModel.CUSTOM_PROPERTY_DEPLOY_CLASSIFIER, target.getClassifier());
        if (props != null) {
            for (String propKey : props.keySet()) {
                String propVal = props.get(propKey);
                SrampModelUtils.setCustomProperty(undeploymentArtifact, propKey, propVal);
            }
        }
        SrampModelUtils.addGenericRelationship(undeploymentArtifact,
                DtgovModel.RELATIONSHIP_DESCRIBED_DEPLOYMENT, artifact.getUuid());
        client.createArtifact(undeploymentArtifact);
    }

}

