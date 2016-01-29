package com.mesosphere.cosmos.endpoint

import com.twitter.util.Future
import io.circe.Encoder
import io.finch.DecodeRequest

import com.mesosphere.cosmos.http.{
  EndpointHandler,
  MediaTypes
}
import com.mesosphere.cosmos.model.mesos.master.MarathonAppsResponse
import com.mesosphere.cosmos.{ AdminRouter, PackageCache }
import com.mesosphere.cosmos.model.{
  Installation,
  ListRequest,
  ListResponse,
  PackageFiles,
  PackageInformation
}

final class ListHandler(
  adminRouter: AdminRouter,
  packageCache: PackageCache
  )(implicit
  requestDecoder: DecodeRequest[ListRequest],
  responseEncoder: Encoder[ListResponse]
  ) extends EndpointHandler[ListRequest, ListResponse] {

  val accepts = MediaTypes.ListRequest
  val produces = MediaTypes.ListResponse

  override def apply(request: ListRequest): Future[ListResponse] = {
    adminRouter.listApps().flatMap { applications =>
      Future.collect {
        applications.apps.map { app =>
          (app.packageReleaseVersion, app.packageSource, app.packageName) match {
            case (Some(releaseVersion), Some(packageSource), Some(packageName)) =>
              if (request.packageName.exists(_ != packageName)) {
                // Package name was specified and it doesn't match
                Future.value(None)
              } else if (request.appId.exists(_ != app.id)) {
                // Application id was specifiedand it doesn't match
                Future.value(None)
              } else {
                // TODO: We should change this to use a package cache collection
                packageCache.getPackageByReleaseVersion(
                  packageName,
                  releaseVersion
                ).map { packageFiles =>
                  Some(
                    Installation(
                      app.id.toString,
                      PackageInformation(
                        releaseVersion,
                        packageSource,
                        packageFiles.packageJson,
                        Option(packageFiles.resourceJson)
                      )
                    )
                  )
                }
              }
            case _ =>
              // TODO: log debug message when one of them is Some.
              Future.value(None)
          }
        }
      } map { installation =>
        ListResponse(installation.flatten)
      }
    }
  }
}