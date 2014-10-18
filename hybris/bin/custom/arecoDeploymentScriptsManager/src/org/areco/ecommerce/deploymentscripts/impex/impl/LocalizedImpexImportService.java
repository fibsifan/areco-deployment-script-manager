/**
 * Copyright 2014 Antonio Robirosa

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.impex.impl;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.Importer;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.impex.ImpexImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * This default implementation uses the impex importer. It allows the configuration of the locale.
 * 
 * @author arobirosa
 * 
 */
@Scope("tenant")
@Service
public class LocalizedImpexImportService implements ImpexImportService {

    public static final String IMPEX_LOCALE_CONF = "deploymentscripts.impex.locale";

    private static final Logger LOG = Logger.getLogger(LocalizedImpexImportService.class);

    @Autowired
    private ConfigurationService configurationService;

    /*
     * { @InheritDoc }
     */
    @Override
    public void importImpexFile(final File impexFile) throws ImpExException {
        ServicesUtil.validateParameterNotNullStandardMessage("impexFile", impexFile);

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(impexFile);
            importImpexFile(inputStream);
        } catch (final FileNotFoundException e) {
            throw new ImpExException(e, "Unable to find the file " + impexFile, 0);
        } catch (final UnsupportedEncodingException e) {
            throw new ImpExException(e, "The file " + impexFile + " must use the UTF-8 encoding.", 0);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    LOG.warn("There was an error clossing the input stream associated to the file " + impexFile, e);
                }
            }
        }
    }

    private void importImpexFile(final InputStream inputStream) throws ImpExException, UnsupportedEncodingException {
        final CSVReader reader = new CSVReader(inputStream, DeploymentScript.DEFAULT_FILE_ENCODING);
        final Importer importer = new Importer(reader);
        final String localeCode = configurationService.getConfiguration().getString(IMPEX_LOCALE_CONF);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Code of the impex locale: '" + localeCode + "'.");
        }
        if (localeCode != null && !localeCode.isEmpty()) {
            importer.getReader().setLocale(LocaleUtils.toLocale(localeCode));
        }
        importer.getReader().enableCodeExecution(true);
        try {
            importer.importAll();
            if (importer.isFinished()) {
                return;
            } else {
                throw new ImpExException("The import of the impex file finished with errors. ");
            }
        } finally {
            importer.close();
        }
    }
}
