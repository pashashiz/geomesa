/***********************************************************************
 * Copyright (c) 2013-2022 Commonwealth Computer Research, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.geomesa.utils.conversions

import org.junit.runner.RunWith
import org.locationtech.geomesa.utils.conversions.ScalaImplicits.RichDouble
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DoubleOpsTest extends Specification {

  "Double" should {

    "be rounded to a given precision" in {
      15.123456.roundAt(3) mustEqual 15.123
      15.123456.roundAt(5) mustEqual 15.12346
    }

    "be truncated to a given precision" in {
      15.123456.truncateAt(3) mustEqual 15.123
      15.123456.truncateAt(5) mustEqual 15.12345
    }
  }
}
