// src/utils/utils.ts

export const parsePrice = (price: string | number): number => {
  if (typeof price === "string") {
    return parseFloat(price.replace("$", ""));
  }
  return price;
};
