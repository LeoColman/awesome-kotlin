import * as React from "react";
import {LinksPageComponent} from "../../components/page_wrapper/page_wrapper";
import {all} from "../../links";
import {useFetch} from "../../useFetch";
import {Links} from "../../model";

export default function Resources() {
  const links = useFetch<Links>("/api/links", [])
  const versions = useFetch<string[]>("/api/kotlin-versions", [])

  const allLinks = all(links.data)

  return <LinksPageComponent
    displayLinks={allLinks}
    searchLinks={allLinks}
    versions={versions.data}
  />
}
