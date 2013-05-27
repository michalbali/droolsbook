##!! this is included in ch4 DSL

[condition][]There is a Customer with firstName {name}=$customer : Customer(firstName == {name})
[consequence][]Greet Customer=System.out.println("Hello " + $customer.getFirstName());



[when][]There is an? {fact}=${fact!lc} : {fact!ucfirst}( )
#[when][]with {matchType} match on {attr}={attr}{matchType!fuzzy?fuzzyMatch/standardMatch} aaaa {attr} aaabbb