package com.mesosphere.cosmos.repository

import com.mesosphere.cosmos.model.{PackageFiles, PackageInfo, UniverseIndexEntry}
import com.twitter.util.Future

/** An aggregation of packages, possibly from multiple repositories.
  *
  * Methods in this trait should be defined to work well even if they must interact with remote
  * services. For example, they should perform filtering on their data before returning it. That
  * way, a hierarchy of [[PackageCollection]]s can work together without generating unwieldy amounts
  * of data.
  */
private trait PackageCollection {

  def getPackageByPackageVersion(packageName: String, packageVersion: Option[String]): Future[PackageFiles]

  def getPackageByReleaseVersion(packageName: String, releaseVersion: String): Future[PackageFiles]

  def getPackageInfo(packageName: String): Future[PackageInfo]

  def search(query: Option[String]): Future[Seq[UniverseIndexEntry]]

}
