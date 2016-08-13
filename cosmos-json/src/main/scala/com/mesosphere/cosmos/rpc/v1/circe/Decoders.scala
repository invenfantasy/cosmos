package com.mesosphere.cosmos.rpc.v1.circe

import com.mesosphere.cosmos.rpc.v1.model._
import com.mesosphere.cosmos.thirdparty.marathon.circe.Decoders._
import com.mesosphere.universe
import com.mesosphere.universe.common.circe.Decoders._
import com.mesosphere.universe.v2.circe.Decoders._
import com.mesosphere.universe.v2.model.{PackageDetailsVersion, ReleaseVersion}
import com.mesosphere.universe.v3.circe.Decoders._
import io.circe.generic.semiauto._
import io.circe.{Decoder, HCursor}

object Decoders {

  implicit val decodeVersionsMap: Decoder[Map[universe.v3.model.PackageDefinition.Version, universe.v3.model.PackageDefinition.ReleaseVersion]] = {
    Decoder.decodeMap[Map, universe.v3.model.PackageDefinition.ReleaseVersion].map { stringKeys =>
      stringKeys.map { case (versionString, releaseVersion) =>
        universe.v3.model.PackageDefinition.Version(versionString) -> releaseVersion
      }
    }
  }

  implicit val decodeSearchResult: Decoder[SearchResult] = deriveDecoder[SearchResult]

  implicit val decodeDescribeRequest: Decoder[DescribeRequest] = deriveDecoder[DescribeRequest]
  implicit val decodeSearchRequest: Decoder[SearchRequest] = deriveDecoder[SearchRequest]
  implicit val decodeSearchResponse: Decoder[SearchResponse] = deriveDecoder[SearchResponse]
  implicit val decodeInstallRequest: Decoder[InstallRequest] = deriveDecoder[InstallRequest]
  implicit val decodeInstallResponse: Decoder[InstallResponse] = deriveDecoder[InstallResponse]
  implicit val decodeUninstallRequest: Decoder[UninstallRequest] = deriveDecoder[UninstallRequest]
  implicit val decodeUninstallResponse: Decoder[UninstallResponse] = deriveDecoder[UninstallResponse]
  implicit val decodeUninstallResult: Decoder[UninstallResult] = deriveDecoder[UninstallResult]

  implicit val decodeRenderRequest: Decoder[RenderRequest] = deriveDecoder[RenderRequest]
  implicit val decodeRenderResponse: Decoder[RenderResponse] = deriveDecoder[RenderResponse]

  implicit val decodeDescribeResponse: Decoder[DescribeResponse] = deriveDecoder[DescribeResponse]
  implicit val decodeListVersionsRequest: Decoder[ListVersionsRequest] = deriveDecoder[ListVersionsRequest]
  implicit val decodeListVersionsResponse: Decoder[ListVersionsResponse] = Decoder.instance { (cursor: HCursor) =>
    for {
      r <- cursor.downField("results").as[Map[String, String]]
    } yield {
      val results = r.map { case (s1, s2) =>
        PackageDetailsVersion(s1) -> ReleaseVersion(s2)
      }
      ListVersionsResponse(results)
    }
  }

  implicit val decodeListRequest: Decoder[ListRequest] = deriveDecoder[ListRequest]
  implicit val decodeListResponse: Decoder[ListResponse] = deriveDecoder[ListResponse]
  implicit val decodeInstallation: Decoder[Installation] = deriveDecoder[Installation]
  implicit val decodeInstalledPackageInformationPackageDetails: Decoder[InstalledPackageInformationPackageDetails] = deriveDecoder[InstalledPackageInformationPackageDetails]
  implicit val decodePackageInformation: Decoder[InstalledPackageInformation] = deriveDecoder[InstalledPackageInformation]

  implicit val decodeCapabilitiesResponse: Decoder[CapabilitiesResponse] = deriveDecoder[CapabilitiesResponse]
  implicit val decodeCapability: Decoder[Capability] = deriveDecoder[Capability]

  implicit val decodePackageRepositoryListRequest: Decoder[PackageRepositoryListRequest] = {
    deriveDecoder[PackageRepositoryListRequest]
  }
  implicit val decodePackageRepositoryListResponse: Decoder[PackageRepositoryListResponse] = {
    deriveDecoder[PackageRepositoryListResponse]
  }
  implicit val decodePackageRepository: Decoder[PackageRepository] = {
    deriveDecoder[PackageRepository]
  }
  implicit val decodePackageRepositoryAddRequest: Decoder[PackageRepositoryAddRequest] = {
    deriveDecoder[PackageRepositoryAddRequest]
  }
  implicit val decodePackageRepositoryAddResponse: Decoder[PackageRepositoryAddResponse] = {
    deriveDecoder[PackageRepositoryAddResponse]
  }
  implicit val decodePackageRepositoryDeleteRequest: Decoder[PackageRepositoryDeleteRequest] = {
    deriveDecoder[PackageRepositoryDeleteRequest]
  }
  implicit val decodePackageRepositoryDeleteResponse: Decoder[PackageRepositoryDeleteResponse] = {
    deriveDecoder[PackageRepositoryDeleteResponse]
  }

}
