[keyword][] Apply after address normalizations = salience -10
[keyword][] Apply after currency conversions = salience -10

#we need the id in conditions
[condition][] legacy {object}-{id} = {object}-{id} 

[condition][] There is {object}-{id} = ${object}{id} : Map( this["_type_"] == "{object}" )

[condition][]unknown conversion value for this currency = not( String() from getConversionToEurFrom($Account1["currency"]) )

[condition][]- has no {field}=this["{field}"] == null
[condition][]- same as {object}-{id} = this == ${object}{id}, eval( ${object}1 != ${object}2 )
[condition][]- country is one of {country_list} = this["country"] in ({country_list})
[condition][]- country is not normalized = eval(!($Address1.get("country") instanceof Address.Country))
[condition][]- "{field}" is same as in {object}-{id}=this["{field}"] == ${object}{id}["{field}"], eval( ${object}1 != ${object}2 )
[condition][]- has {field} different to {value}= this["{field}"] != null && != "{value}"

#we don't need id in consequences
[consequence][] legacy {object}-{id} = ${object}{id}



#add more of these 'and' as needed
#[consequence][] and {object} =, ${object} 
[consequence][]Display {message_type_enum} for {object}=validationReport.addMessage(reportFactory.createMessage(Message.Type.{message_type_enum}, kcontext.getRule().getName(), {object}));
[consequence][]remove {object} = retract( {object} );
[consequence][]set country to {country}=set country to Address.Country.{country}
[consequence][]for {object}set {field} to {value} = modify( {object} ) \{ put("{field}", {value}  ) \}
[consequence][]sum balances=((BigDecimal)$Account1.get("balance")).add((BigDecimal)$Account2.get("balance"))