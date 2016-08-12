package com.mesosphere.cosmos.http

import cats.data.Xor
import com.mesosphere.cosmos.http.FinchExtensions.RequestReaderOps
import com.twitter.finagle.http.Request
import com.twitter.util.{Await, Future, Return, Throw}
import io.finch.Error.NotValid
import io.finch.items.RequestItem
import io.finch.{Endpoint, items, Output, Input}
import org.scalatest.FreeSpec
import cats.Eval

final class RequestReaderOpsSpec extends FreeSpec {

  import RequestReaderOpsSpec._

  "RequestReaderOps.convert[B](A => String Xor B) should" - {


    "forward the success value from the function argument" in {
      val result = request(true).convert(exampleFn)(DummyRequest)
      assertResult(())(unpack(result))
    }

    "wrap the failure message from the function argument in an exception" in {
      val result = request(false).convert(exampleFn)(DummyRequest)
      val Throw(NotValid(_, rule)) = Await.result(result.liftToTry)
      assertResult("failure")(rule)
    }

    "include the item from the base reader in failures" in {
      val result = request(false).convert(exampleFn)(DummyRequest)
      val Throw(NotValid(item, _)) = Await.result(result.liftToTry)
      assertResult(items.BodyItem)(item)
    }
  }
}

object RequestReaderOpsSpec {
  val DummyRequest: Request = Request("http://some.host")
  private def exampleFn(a: Boolean): String Xor Unit = if (a) Xor.Right(()) else Xor.Left("failure")

  private def request(bool: Boolean):Endpoint[Boolean] = {
    new Endpoint[Boolean] {
      val item: RequestItem = items.BodyItem
      def apply(req: Request): Future[Boolean] = Future.value(bool)
    }
  }

  private def unpack[A](result: Eval[Future[Output[A]]]): A = {
    val future = result.value
    val output = Await.result(future)
    output.value
  }
}
