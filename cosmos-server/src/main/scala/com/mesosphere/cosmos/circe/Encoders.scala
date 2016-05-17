package com.mesosphere.cosmos.circe

import java.nio.ByteBuffer
import java.util.Base64

import cats.data.Ior
import com.netaporter.uri.Uri
import com.twitter.finagle.http.Status
import io.circe.generic.semiauto._
import io.circe.Encoder
import io.circe.Json
import io.circe.JsonObject
import io.circe.ObjectEncoder
import io.circe.syntax.EncoderOps
import io.finch.Error

import com.mesosphere.cosmos._
import com.mesosphere.cosmos.http.MediaType
import com.mesosphere.cosmos.model._
import com.mesosphere.cosmos.model.thirdparty.marathon._
import com.mesosphere.cosmos.model.thirdparty.mesos.master._
import com.mesosphere.cosmos.raml.Body
import com.mesosphere.cosmos.raml.DataType
import com.mesosphere.cosmos.raml.Document
import com.mesosphere.cosmos.raml.Method
import com.mesosphere.cosmos.raml.Response
import com.mesosphere.cosmos.raml.{Resource => RamlResource}
import com.mesosphere.universe._

object Encoders {
  implicit val encodeLicense: Encoder[License] = deriveFor[License].encoder
  implicit val encodePackageDefinition: Encoder[PackageDetails] = deriveFor[PackageDetails].encoder
  implicit val encodeContainer: Encoder[Container] = deriveFor[Container].encoder
  implicit val encodeAssets: Encoder[Assets] = deriveFor[Assets].encoder
  implicit val encodeImages: Encoder[Images] = Encoder.instance { (images: Images) =>
    Json.obj(
      "icon-small" -> images.iconSmall.asJson,
      "icon-medium" -> images.iconMedium.asJson,
      "icon-large" -> images.iconLarge.asJson,
      "screenshots" -> images.screenshots.asJson
    )
  }
  implicit val encodeResource: Encoder[Resource] = deriveFor[Resource].encoder

  implicit val encodePackageIndex: Encoder[UniverseIndexEntry] = ObjectEncoder.instance { entry =>
    val encodedFields = encodeIndexEntryFields(
      entry.name,
      entry.currentVersion,
      entry.versions,
      entry.description,
      entry.framework,
      entry.tags,
      entry.promoted
    )
    JsonObject.fromIndexedSeq(encodedFields)
  }

  implicit val encodeSearchResult: Encoder[SearchResult] = ObjectEncoder.instance { searchResult =>
    val encodedFields = encodeIndexEntryFields(
      searchResult.name,
      searchResult.currentVersion,
      searchResult.versions,
      searchResult.description,
      searchResult.framework,
      searchResult.tags,
      searchResult.promoted
    )
    JsonObject.fromIndexedSeq(encodedFields :+ ("images" -> searchResult.images.asJson))
  }

  implicit val encodeUniverseIndex: Encoder[UniverseIndex] = deriveFor[UniverseIndex].encoder
  implicit val encodeMasterState: Encoder[MasterState] = deriveFor[MasterState].encoder
  implicit val encodeFramework: Encoder[Framework] = deriveFor[Framework].encoder
  implicit val encodeMesosFrameworkTearDownResponse: Encoder[MesosFrameworkTearDownResponse] = deriveFor[MesosFrameworkTearDownResponse].encoder
  implicit val encodeMarathonAppResponse: Encoder[MarathonAppResponse] = deriveFor[MarathonAppResponse].encoder
  implicit val encodeMarathonAppsResponse: Encoder[MarathonAppsResponse] = deriveFor[MarathonAppsResponse].encoder
  implicit val encoder: Encoder[AppId] = Encoder.instance(_.toString.asJson)
  implicit val encodeMarathonAppContainer: Encoder[MarathonAppContainer] = deriveFor[MarathonAppContainer].encoder
  implicit val encodeMarathonAppContainerDocker: Encoder[MarathonAppContainerDocker] = deriveFor[MarathonAppContainerDocker].encoder
  implicit val encodeMarathonApp: Encoder[MarathonApp] = deriveFor[MarathonApp].encoder
  implicit val encodeDescribeRequest: Encoder[DescribeRequest] = deriveFor[DescribeRequest].encoder
  implicit val encodePackageFiles: Encoder[PackageFiles] = deriveFor[PackageFiles].encoder
  implicit val encodeSearchRequest: Encoder[SearchRequest] = deriveFor[SearchRequest].encoder
  implicit val encodeSearchResponse: Encoder[SearchResponse] = deriveFor[SearchResponse].encoder
  implicit val encodeInstallRequest: Encoder[InstallRequest] = deriveFor[InstallRequest].encoder
  implicit val encodeInstallResponse: Encoder[InstallResponse] = deriveFor[InstallResponse].encoder
  implicit val encodeUninstallRequest: Encoder[UninstallRequest] = deriveFor[UninstallRequest].encoder
  implicit val encodeUninstallResponse: Encoder[UninstallResponse] = deriveFor[UninstallResponse].encoder
  implicit val encodeUninstallResult: Encoder[UninstallResult] = deriveFor[UninstallResult].encoder

  implicit val encodeRenderRequest: Encoder[RenderRequest] = deriveFor[RenderRequest].encoder
  implicit val encodeRenderResponse: Encoder[RenderResponse] = deriveFor[RenderResponse].encoder

  implicit val encodeCommandDefinition: Encoder[Command] = deriveFor[Command].encoder
  implicit val encodeDescribeResponse: Encoder[DescribeResponse] = deriveFor[DescribeResponse].encoder
  implicit val encodeListVersionsRequest: Encoder[ListVersionsRequest] = deriveFor[ListVersionsRequest].encoder
  implicit val encodeListVersionsResponse: Encoder[ListVersionsResponse] = ObjectEncoder.instance( response => {
    JsonObject.singleton("results", encodeMap(response.results))
  })

  implicit val encodeErrorResponse: Encoder[ErrorResponse] = deriveFor[ErrorResponse].encoder
  implicit val encodeMarathonError: Encoder[MarathonError] = deriveFor[MarathonError].encoder

  implicit val encodeUri: Encoder[Uri] = Encoder.instance(_.toString.asJson)

  implicit val encodeListRequest: Encoder[ListRequest] = deriveFor[ListRequest].encoder
  implicit val encodeListResponse: Encoder[ListResponse] = deriveFor[ListResponse].encoder
  implicit val encodeInstallation: Encoder[Installation] = deriveFor[Installation].encoder
  implicit val encodePackageInformation: Encoder[InstalledPackageInformation] = deriveFor[InstalledPackageInformation].encoder

  implicit val encodeUniverseVersion: Encoder[UniverseVersion] = Encoder.instance(_.toString.asJson)
  implicit val encodePackagingVersion: Encoder[PackagingVersion] = Encoder.instance(_.toString.asJson)
  implicit val encodePackageRevision: Encoder[ReleaseVersion] = Encoder.instance(_.toString.asJson)
  implicit val encodePackageDetailsVersion: Encoder[PackageDetailsVersion] = Encoder.instance(_.toString.asJson)

  implicit val encodeCapabilitiesResponse: Encoder[CapabilitiesResponse] = deriveFor[CapabilitiesResponse].encoder
  implicit val encodeCapability: Encoder[Capability] = deriveFor[Capability].encoder

  implicit val encodePackageRepositoryListRequest: Encoder[PackageRepositoryListRequest] = {
    deriveFor[PackageRepositoryListRequest].encoder
  }
  implicit val encodePackageRepositoryListResponse: Encoder[PackageRepositoryListResponse] = {
    deriveFor[PackageRepositoryListResponse].encoder
  }
  implicit val encodePackageRepository: Encoder[PackageRepository] = {
    deriveFor[PackageRepository].encoder
  }
  implicit val encodePackageRepositoryAddRequest: Encoder[PackageRepositoryAddRequest] = {
    deriveFor[PackageRepositoryAddRequest].encoder
  }
  implicit val encodePackageRepositoryAddResponse: Encoder[PackageRepositoryAddResponse] = {
    deriveFor[PackageRepositoryAddResponse].encoder
  }
  implicit val encodePackageRepositoryDeleteRequest: Encoder[PackageRepositoryDeleteRequest] = {
    deriveFor[PackageRepositoryDeleteRequest].encoder
  }
  implicit val encodePackageRepositoryDeleteResponse: Encoder[PackageRepositoryDeleteResponse] = {
    deriveFor[PackageRepositoryDeleteResponse].encoder
  }

  implicit val encodeZooKeeperStorageEnvelope: Encoder[ZooKeeperStorageEnvelope] =
    deriveFor[ZooKeeperStorageEnvelope].encoder

  implicit val exceptionEncoder: Encoder[Exception] = {
    Encoder.instance { e => exceptionErrorResponse(e).asJson }
  }

  implicit val encodeByteBuffer: Encoder[ByteBuffer] = Encoder.instance { bb =>
    Base64.getEncoder.encodeToString(ByteBuffers.getBytes(bb)).asJson
  }

  // RAML decoders

  implicit val encodeDocument: Encoder[Document] = ObjectEncoder.instance { document =>
    JsonObject.fromMap {
      document.resources.mapValues(_.asJson) + (("title", document.title.asJson))
    }
  }

  implicit val encodeRamlResource: Encoder[RamlResource] = deriveFor[RamlResource].encoder

  implicit val encodeMethod: Encoder[Method] = ObjectEncoder.instance { method =>
    JsonObject.fromMap {
      method.responses.map { case (status, response) =>
        (status.code.toString, response.asJson)
      } + (
        ("body", method.body.asJson)
      ) + (
        ("description", method.description.asJson)
      )
    }
  }

  implicit val encodeBody: Encoder[Body] = ObjectEncoder.instance { body =>
    JsonObject.fromMap {
      body.content.map { case (mediaType, dataType) =>
        (mediaType.show, dataType.asJson)
      }
    }
  }

  implicit val encodeStatus: Encoder[Status] = Encoder.instance(_.code.toString.asJson)

  implicit val encodeDataType: Encoder[DataType] = ???

  implicit val encodeResponse: Encoder[Response] = ???



  // Helpers methods

  private[this] def exceptionErrorResponse(t: Throwable): ErrorResponse = t match {
    case Error.NotPresent(item) =>
      ErrorResponse("not_present", s"Item '${item.description}' not present but required")
    case Error.NotParsed(item, typ, cause) =>
      ErrorResponse("not_parsed", s"Item '${item.description}' unable to be parsed : '${cause.getMessage}'")
    case Error.NotValid(item, rule) =>
      ErrorResponse("not_valid", s"Item '${item.description}' deemed invalid by rule: '$rule'")
    case Error.RequestErrors(ts) =>
      val details = ts.map(exceptionErrorResponse).toList.asJson
      ErrorResponse(
        "multiple_errors",
        "Multiple errors while processing request",
        Some(JsonObject.singleton("errors", details))
      )
    case ce: CosmosError =>
      ErrorResponse(ce.getClass.getSimpleName, msgForCosmosError(ce), ce.getData)
    case t: Throwable =>
      ErrorResponse("unhandled_exception", t.getMessage)
  }

  private[this] def msgForCosmosError(err: CosmosError): String = err match {
    case PackageNotFound(packageName) =>
      s"Package [$packageName] not found"
    case VersionNotFound(packageName, PackageDetailsVersion(packageVersion)) =>
      s"Version [$packageVersion] of package [$packageName] not found"
    case EmptyPackageImport() =>
      "Package is empty"
    case PackageFileMissing(fileName, _) =>
      s"Package file [$fileName] not found"
    case PackageFileNotJson(fileName, parseError) =>
      s"Package file [$fileName] is not JSON: $parseError"
    case PackageFileSchemaMismatch(fileName) =>
      s"Package file [$fileName] does not match schema"
    case PackageAlreadyInstalled() =>
      "Package is already installed"
    case MarathonBadResponse(marathonErr) => marathonErr.message
    case MarathonGenericError(marathonStatus) =>
      s"Received response status code ${marathonStatus.code} from Marathon"
    case MarathonBadGateway(marathonStatus) =>
      s"Received response status code ${marathonStatus.code} from Marathon"
    case IndexNotFound(repoUri) =>
      s"Index file missing for repo [$repoUri]"
    case RepositoryNotFound(repoUri) =>
      s"No repository found [$repoUri]"
    case MarathonAppMetadataError(note) => note
    case MarathonAppDeleteError(appId) =>
      s"Error while deleting marathon app '$appId'"
    case MarathonAppNotFound(appId) =>
      s"Unable to locate service with marathon appId: '$appId'"
    case CirceError(cerr) => cerr.getMessage
    case MesosRequestError(note) => note
    case JsonSchemaMismatch(_) =>
      "Options JSON failed validation"
    case UnsupportedContentType(supported, actual) =>
      val acceptMsg = supported.map(_.show).mkString("[", ", ", "]")
      actual match {
        case Some(mt) =>
          s"Unsupported Content-Type: ${mt.show} Accept: $acceptMsg"
        case None =>
          s"Unspecified Content-Type Accept: $acceptMsg"
      }
    case GenericHttpError(method, uri, status) =>
      s"Unexpected down stream http error: ${method.getName} ${uri.toString} ${status.code}"
    case AmbiguousAppId(pkgName, appIds) =>
      s"Multiple apps named [$pkgName] are installed: [${appIds.mkString(", ")}]"
    case MultipleFrameworkIds(pkgName, pkgVersion, fwName, ids) =>
      pkgVersion match {
        case Some(ver) =>
          s"Uninstalled package [$pkgName] version [$ver]\n" +
            s"Unable to shutdown [$pkgName] service framework with name [$fwName] because there are multiple framework " +
            s"ids matching this name: [${ids.mkString(", ")}]"
        case None =>
          s"Uninstalled package [$pkgName]\n" +
            s"Unable to shutdown [$pkgName] service framework with name [$fwName] because there are multiple framework " +
            s"ids matching this name: [${ids.mkString(", ")}]"
      }
    case NelErrors(nelE) => nelE.toString
    case FileUploadError(msg) => msg
    case PackageNotInstalled(pkgName) =>
      s"Package [$pkgName] is not installed"
    case UninstallNonExistentAppForPackage(pkgName, appId) =>
      s"Package [$pkgName] with id [$appId] is not installed"

    case ServiceUnavailable(serviceName, _) =>
      s"Unable to complete request due to downstream service [$serviceName] unavailability"
    case IncompleteUninstall(packageName, _) =>
      s"Incomplete uninstall of package [$packageName] due to Mesos unavailability"

    case RepoNameOrUriMissing() =>
      s"Must specify either the name or URI of the repository"
    case ZooKeeperStorageError(msg) => msg
    case ConcurrentAccess(_) =>
      s"Retry operation. Operation didn't complete due to concurrent access."
    case RepositoryAlreadyPresent(nameOrUri) =>
      nameOrUri match {
        case Ior.Both(n, u) =>
          s"Repository name [$n] and URI [$u] are both already present in the list"
        case Ior.Left(n) => s"Repository name [$n] is already present in the list"
        case Ior.Right(u) => s"Repository URI [$u] is already present in the list"
      }
    case RepositoryAddIndexOutOfBounds(attempted, max) =>
      s"Index out of range: $attempted"
    case UnsupportedRepositoryVersion(version) => s"Repository version [$version] is not supported"
    case UnsupportedRepositoryUri(uri) => s"Repository URI [$uri] uses an unsupported scheme. " +
      "Only http and https are supported"
    case InvalidRepositoryUri(repository, _) =>
      s"URI for repository [${repository.name}] is invalid: ${repository.uri}"
    case RepositoryNotPresent(nameOrUri) =>
      nameOrUri match {
        case Ior.Both(n, u) => s"Neither repository name [$n] nor URI [$u] are present in the list"
        case Ior.Left(n) => s"Repository name [$n] is not present in the list"
        case Ior.Right(u) => s"Repository URI [$u] is not present in the list"
      }
  }

  private[this] def encodeMap(versions: Map[PackageDetailsVersion, ReleaseVersion]): Json = {
    versions
      .map {
        case (PackageDetailsVersion(pdv), ReleaseVersion(rv)) => pdv -> rv
      }.asJson
  }

  private[this] def encodeIndexEntryFields(
    name: String,
    currentVersion: PackageDetailsVersion,
    versions: Map[PackageDetailsVersion, ReleaseVersion],
    description: String,
    framework: Boolean,
    tags: List[String],
    promoted: Option[Boolean]
  ): Vector[(String, Json)] = {
    Vector(
      "name" -> name.asJson,
      "currentVersion" -> currentVersion.asJson,
      "versions" -> encodeMap(versions),
      "description" -> description.asJson,
      "framework" -> framework.asJson,
      "tags" -> tags.asJson,
      "promoted" -> promoted.asJson
    )
  }

}
