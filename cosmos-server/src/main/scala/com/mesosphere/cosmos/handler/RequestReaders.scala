package com.mesosphere.cosmos.handler

import cats.syntax.option._
import com.mesosphere.cosmos.circe.{DispatchingMediaTypedEncoder, MediaTypedDecoder, MediaTypedEncoder}
import com.mesosphere.cosmos.http.FinchExtensions._
import com.mesosphere.cosmos.http.{Authorization, MediaType, RequestSession}
import io.finch._
import shapeless.{::, HNil}

object RequestReaders {

  def noBody[Res](implicit
    produces: DispatchingMediaTypedEncoder[Res]
  ): Endpoint[EndpointContext[Unit, Res]] = {
    baseReader(produces).map { case (session, responseEncoder) =>
      EndpointContext((), session, responseEncoder)
    }
  }

  def standard[Req, Res](implicit
    accepts: MediaTypedDecoder[Req],
    produces: DispatchingMediaTypedEncoder[Res]
  ): Endpoint[EndpointContext[Req, Res]] = {
    val r = baseReader(produces)
    val h = header("Content-Type").as[MediaType].should(beTheExpectedType(accepts.mediaType))
    val b = body.as[Req](accepts.decoder, accepts.classTag)
    val c = (r :: h :: b)
    c.map {
      case (reqSession, responseEncoder) :: _ :: req  :: HNil => EndpointContext(req, reqSession, responseEncoder)
    }
/* 
    for {
      (reqSession, responseEncoder) <- baseReader(produces)
      _ <- header("Content-Type").as[MediaType].should(beTheExpectedType(accepts.mediaType))
      req <- body.as[Req](accepts.decoder, accepts.classTag)
    } yield {
      EndpointContext(req, reqSession, responseEncoder)
    }
*/
  }

  private[this] def baseReader[Res](
    produces: DispatchingMediaTypedEncoder[Res]
  ): Endpoint[(RequestSession, MediaTypedEncoder[Res])] = {
    for {
      responseEncoder <- header("Accept")
        .as[MediaType]
        .convert { accept =>
          produces(accept)
            .toRightXor(s"should match one of: ${produces.mediaTypes.map(_.show).mkString(", ")}")
        }
      auth <- headerOption("Authorization")
    } yield {
      (RequestSession(auth.map(Authorization(_))), responseEncoder)
    }
  }

}
