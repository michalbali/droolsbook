[condition][] The Customer = $Customer : Customer() 
[condition][] The Account = $Account : Account() 
 
[condition][]- has no {field}={field} == null 

[condition][] or it is blank = == "" ||
[condition][] phone number = phoneNumber


#add more of these 'and' as needed
[consequence][] and {object} =, ${object} 
[consequence][]Display {message_type} for {object}={message_type}( kcontext, ${object} );
[consequence][]Display {message_type}={message_type}( kcontext );