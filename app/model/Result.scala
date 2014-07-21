package model

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.json.__
   
/**
 * Created by elenko on 12.07.14.
 */
case class Result (
    id: String,
	name: String,
	address: String,
	rating: String = "0"
	) extends Ordered[Result] {

	def compare(other: Result) = {
		(other.rating.toDouble - this.rating.toDouble).*(100).toInt
  	}
}
		  
object Result {

	def apply(id: String, name: String, address: String, rating: Option[String]): Result = 
		Result(id, name, address, rating.getOrElse("0"))
	
	implicit val reader: Reads[Result] = (
		    (__ \ "id").read[String] and 
		    (__ \ "name").read[String] and
		    (__ \ "address").read[String] and
		    (__ \ "rating").readNullable[String]
		  )(Result.apply(_: String, _: String, _: String, _: Option[String]))

	implicit val wr: Writes[Result] = (
			(__ \ "_id").write[String] and
			(__ \ "name").write[String] and
			(__ \ "address").write[String] and
			(__ \ "rating").write[String]
			)(unlift(Result.unapply))
}
