declare module '*.jpeg' {
  const value: string;
  export default value;
}

export interface Images {
  [key: string]: string;
}
