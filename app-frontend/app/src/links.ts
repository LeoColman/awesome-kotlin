import {Category, Links, LinkState} from "./model";

export const kugs: (Links) => Links = (data) => data.filter(it => it.name == "Kotlin User Groups").map(sortLinks)
export const all: (Links) => Links = (data) => data.filter(it => it.name != "Kotlin User Groups").map(sortLinks)
export const awesome: (Links) => Links = (data) => data.map(awesomeLinks)
  .filter(category => category.subcategories.length > 0)
  .map(sortLinks);

function sortLinks(category: Category): Category {
  return {
    name: category.name,
    subcategories: category.subcategories.sort((a, b) => {
      return b.links.length - a.links.length
    })
  }
}

function awesomeLinks(category: Category): Category {
  return {
    name: category.name,
    subcategories: category.subcategories
      .map(subcategory => {
        return {
          name: subcategory.name,
          links: subcategory.links.filter(link => link.state === LinkState.AWESOME)
        };
      })
      .filter(subcategory => subcategory.links.length > 0)
  }
}
