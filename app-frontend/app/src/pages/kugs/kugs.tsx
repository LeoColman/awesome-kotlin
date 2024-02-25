import * as React from "react";
import {LinksPageComponent} from "../../components/page_wrapper/page_wrapper";
import {kugs} from "../../links";
import {useFetch} from "../../useFetch";
import {Links} from "../../model";

export default function Kugs() {
  const links = useFetch<Links>("/api/links", [])
  const versions = useFetch<string[]>("/api/kotlin-versions", [])

  const kugLinks = kugs(links.data)

  return <LinksPageComponent
    displayLinks={kugLinks}
    searchLinks={kugLinks}
    versions={versions.data}
  />
}
