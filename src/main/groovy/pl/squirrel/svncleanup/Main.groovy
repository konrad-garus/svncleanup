package pl.squirrel.svncleanup

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder


class Main {
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		println("Login:")
		def login = br.readLine();
		println("Pass:")
		def pwd = br.readLine();
		def http = new HTTPBuilder('https://mwmllc.svn.cvsdude.com/trx/')
		http.auth.basic login, pwd
		def res = http.get(path: 'branches')
		def myBranches = res.'**'.findAll {
			it.name() == "LI" && it.text().contains("konradg")
		}.collect {
			it.text()
		}
		println myBranches
		def myTickets = [] as Set
		myBranches.each {
			branch ->
			branch.split("[^0-9]+").each {
				token ->
				if(token.matches(/\d{4}/)) {
					myTickets.add(token)
				}
			}
		}
		def http2 = new HTTPBuilder('https://mwmllc.trac.cvsdude.com/trx/')
		http2.auth.basic login, pwd
		myTickets.each {
			tracid ->
			println http2.get(path: "query", contentType: ContentType.TEXT, query: [id: tracid, format: "tab", col: ["id", "status", "milestone", "changetime", "summary"]]).getText()
		}
	}
}
