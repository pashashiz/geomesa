/***********************************************************************
 * Copyright (c) 2013-2022 Commonwealth Computer Research, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.geomesa.web.core

import org.scalatra.ScalatraServlet
import org.scalatra.swagger.{ApiInfo, ContactInfo, LicenseInfo, NativeSwaggerBase, Swagger}

class ResourcesApp(val swagger: Swagger) extends ScalatraServlet with NativeSwaggerBase

object GeoMesaApiInfo extends ApiInfo(
  "The GeoMesa REST API",
  description = "Docs for the GeoMesa REST API",
  termsOfServiceUrl = "http://geomesa.org",
  contact = ContactInfo("GeoMesa", "https://www.geomesa.org", "geomesa-users@locationtech.org"),
  license = LicenseInfo("Apache License, Version 2.0", "http://www.apache.org/licenses"))

class GeoMesaSwagger extends Swagger(Swagger.SpecVersion, "1.0.0", GeoMesaApiInfo)
