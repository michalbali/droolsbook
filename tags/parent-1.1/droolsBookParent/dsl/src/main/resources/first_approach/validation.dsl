#keyword shoudl be used for keyword substitutions :), don't do the following :)
#[keyword][]- {rule_name} : when The {object} does not have {field} then Display {message_type} = rule {rule_name} when ${object} : {object}( {field} == null ) then {message_type}(drools); end

[condition][] or it is blank =  == "" || 
[condition][]The {object} does not have {field}=${object} : {object}( {field} == null )

[condition][]phone number=phoneNumber


#add more of these 'and' as needed
#[consequence][] and {object} =, ${object} 
[consequence][]Display {message_type} for {object}={message_type}( drools, ${object} );
[consequence][]Display {message_type}={message_type}( drools );