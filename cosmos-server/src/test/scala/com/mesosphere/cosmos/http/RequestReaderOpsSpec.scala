package com.mesosphere.cosmos.http

import cats.data.Xor
import com.mesosphere.cosmos.http.FinchExtensions.RequestReaderOps
import com.twitter.finagle.http.Request
import com.twitter.util.{Await, Future, Return, Throw, Try}
import io.finch.Error.NotValid
import io.finch.items.RequestItem
import io.finch.{Endpoint, items, Output, Input}
import org.scalatest.FreeSpec
import cats.{Eval,Now}

final class RequestReaderOpsSpec extends FreeSpec {

  import RequestReaderOpsSpec._

  "RequestReaderOps.convert[B](A => String Xor B) should" - {


    "forward the success value from the function argument" in {
      val result = request(true).convert(exampleFn)(Input(DummyRequest)).get._2
      assertResult(())(unpack(result))
    }

    "wrap the failure message from the function argument in an exception" in {
      val result = request(false).convert(exampleFn)(Input(DummyRequest))
      val Throw(NotValid(_, rule)) = Try(unpack(result.get._2))
      assertResult("failure")(rule)
    }

    "include the item from the base reader in failures" in {
      val result = request(false).convert(exampleFn)(Input(DummyRequest))
      val Throw(NotValid(item, _)) = Try(unpack(result.get._2))
      assertResult(items.BodyItem)(item)
    }
  }
}

object RequestReaderOpsSpec {
  val DummyRequest: Request = Request("http://some.host")
  private def exampleFn(a: Boolean): String Xor Unit = if (a) Xor.Right(()) else Xor.Left("failure")

  private def request(bool: Boolean):Endpoint[Boolean] = {
    new Endpoint[Boolean] {
      override val item: RequestItem = items.BodyItem
      def apply(req: Input): Endpoint.Result[Boolean] = Some((req, Now(Future.value(Output.payload(bool)))))
    }
  }

  private def unpack[A](result: Eval[Future[Output[A]]]): A = {
    val future = result.value
    val output = Await.result(future)
    output.value
  }
}
