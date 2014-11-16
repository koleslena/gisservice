package utils

import java.net.URLEncoder

import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import play.api.Play

/**
 * Created by elenko on 22.06.14.
 */
object UrlStore {
  val KeyFof2GisApi = Play.current.configuration.getString("conf.api.key").get
  val cityRequestUrl = Play.current.configuration.getString("conf.company.request.url").get
  val ratingRequestUrl = Play.current.configuration.getString("conf.profile.request.url").get
  val VersionFof2GisApi = Play.current.configuration.getString("conf.api.version").get

  def encode(str: String) = URLEncoder.encode(str, "UTF8")

  def urlForSearch(city: String, fir: String): String = {
    f"$cityRequestUrl?key=$KeyFof2GisApi&what=${encode(fir)}&where=${encode(city)}&sort=rating&version=$VersionFof2GisApi"
  }

  def urlForProfile(id: String): String = {
    f"$ratingRequestUrl?key=$KeyFof2GisApi&id=$id&version=$VersionFof2GisApi"
  }

}
