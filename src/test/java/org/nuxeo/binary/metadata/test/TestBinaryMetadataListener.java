/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *      Vladimir Pasquier <vpasquier@nuxeo.com>
 */
package org.nuxeo.binary.metadata.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.automation.core.util.DocumentHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

/**
 * @since 7.1
 */
@RunWith(FeaturesRunner.class)
@Features(BinaryMetadataFeature.class)
@LocalDeploy({ "org.nuxeo.binary.metadata.test:OSGI-INF/binary-metadata-contrib-test.xml" })
@RepositoryConfig(cleanup = Granularity.METHOD)
public class TestBinaryMetadataListener {

    @Inject
    CoreSession session;

    @Test
    public void testSyncListener() {
        // Create folder
        DocumentModel doc = session.createDocumentModel("/", "folder", "Folder");
        doc.setPropertyValue("dc:title", "Folder");
        session.createDocument(doc);

        // Create file
        doc = session.createDocumentModel("/folder", "file", "File");
        doc.setPropertyValue("dc:title", "file");
        doc = session.createDocument(doc);

        // Attach PDF
        File binary = FileUtils.getResourceFileFromContext("data/hello.pdf");
        FileBlob fb = new FileBlob(binary);
        fb.setMimeType("application/pdf");
        DocumentHelper.addBlob(doc.getProperty("file:content"), fb);
        session.saveDocument(doc);

        DocumentModel pdfDoc = session.getDocument(doc.getRef());

        assertEquals("en-US", pdfDoc.getPropertyValue("dc:title"));
        assertEquals("OpenOffice.org", pdfDoc.getPropertyValue("dc:source"));
        assertEquals("30 kB", pdfDoc.getPropertyValue("dc:description"));
    }

}
