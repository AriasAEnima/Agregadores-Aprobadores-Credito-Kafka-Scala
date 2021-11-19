package eci.edu.co.commands


import play.api.libs.json.JsValue

trait CommandCallAPI{
  def callAPI(body : JsValue) : (String,JsValue)
}
