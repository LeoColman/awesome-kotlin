import * as React from "react";
import {LinksPageComponent} from "../../components/page_wrapper/page_wrapper";
import {all, awesome} from "../../links";
import {useFetch} from "../../useFetch";
import {Links} from "../../model";

export default function Home() {
  const links = useFetch<Links>("/api/links", [])
  const versions = useFetch<string[]>("/api/kotlin-versions", [])

  const awesomeLinks = awesome(links.data)
  const allLinks = all(links.data)

  return <LinksPageComponent
    displayLinks={awesomeLinks}
    searchLinks={allLinks}
    versions={versions.data}
  />
}
