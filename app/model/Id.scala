package model

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.__

/**
 * Created by elenko on 12.07.14.
 */
case class Id (
    id: String,
    name: String
	)

object Id {
	implicit val reader: Reads[Id] = (
		    (__ \ "id").read[String] and 
		    (__ \ "name").read[String] )(Id(_:String, _:String))
}