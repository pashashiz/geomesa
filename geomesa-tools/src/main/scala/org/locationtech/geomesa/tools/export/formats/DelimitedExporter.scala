/***********************************************************************
 * Copyright (c) 2013-2022 Commonwealth Computer Research, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.geomesa.tools.export.formats

import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.csv.{CSVFormat, CSVPrinter, QuoteMode}
import org.locationtech.geomesa.tools.`export`.formats.FeatureExporter.ExportStream
import org.locationtech.geomesa.tools.export.formats.FeatureExporter.ByteCounterExporter
import org.locationtech.geomesa.utils.geotools.SimpleFeatureTypes
import org.locationtech.geomesa.utils.text.WKTUtils
import org.locationtech.jts.geom.Geometry
import org.opengis.feature.simple.{SimpleFeature, SimpleFeatureType}

import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.time.{Instant, ZoneOffset}
import java.util.Date

class DelimitedExporter(stream: ExportStream, format: CSVFormat, withHeader: Boolean, includeIds: Boolean)
    extends ByteCounterExporter(stream) with LazyLogging {

  import org.locationtech.geomesa.utils.geotools.GeoToolsDateFormat

  import scala.jdk.CollectionConverters._

  private var printer: CSVPrinter = _

  override def start(sft: SimpleFeatureType): Unit = {
    printer = format.print(new OutputStreamWriter(stream.os, StandardCharsets.UTF_8))
    // write out a header line
    if (withHeader) {
      if (includeIds) {
        printer.print("id")
      }
      sft.getAttributeDescriptors.asScala.foreach { descriptor =>
        printer.print(SimpleFeatureTypes.encodeDescriptor(sft, descriptor))
      }
      printer.println()
      printer.flush()
    }
  }

  override def export(features: Iterator[SimpleFeature]): Option[Long] = {
    var count = 0L
    features.foreach { sf =>
      if (includeIds) {
        printer.print(sf.getID)
      }
      var i = 0
      while (i < sf.getAttributeCount) {
        printer.print(stringify(sf.getAttribute(i)))
        i += 1
      }
      printer.println()

      count += 1
      if (count % 10000 == 0) {
        logger.debug(s"wrote $count features")
      }
    }

    printer.flush()

    logger.debug(s"Exported $count features")
    Some(count)
  }

  override def close(): Unit =  if (printer != null) { printer.close() }

  private def stringify(obj: Any): String = obj match {
    case null                   => ""
    case g: Geometry            => WKTUtils.write(g)
    case d: Date                => GeoToolsDateFormat.format(Instant.ofEpochMilli(d.getTime).atZone(ZoneOffset.UTC))
    case l: java.util.List[_]   => l.asScala.map(stringify).mkString(",")
    case m: java.util.Map[_, _] => m.asScala.map { case (k, v) => s"${stringify(k)}->${stringify(v)}"}.mkString(",")
    case _                      => obj.toString
  }
}

object DelimitedExporter {

  def csv(stream: ExportStream, withHeader: Boolean, includeIds: Boolean = true): DelimitedExporter =
    new DelimitedExporter(stream, CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL), withHeader, includeIds)

  def tsv(stream: ExportStream, withHeader: Boolean, includeIds: Boolean = true): DelimitedExporter =
    new DelimitedExporter(stream, CSVFormat.TDF.withQuoteMode(QuoteMode.MINIMAL), withHeader, includeIds)
}
