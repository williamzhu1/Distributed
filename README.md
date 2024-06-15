# Meditrade App

The Meditrade App is a React-based web application designed to facilitate the buying and selling of medical products across different countries.

## Project Setup

### Prerequisites

Before you begin, ensure you have the following installed on your system:

- Node.js (v14 or higher)
- npm (comes with Node.js)

You can check your Node and npm versions by running:

```bash
node -v
npm -v
```

### Getting Started

Follow these steps to set up the project locally.

#### Clone the Repository

First, clone the project repository from GitHub to your local machine:

```bash
git clone https://github.com/your-username/meditrade-app.git
cd meditrade-app
```

#### Install Dependencies

Navigate to the project directory and install the required npm packages:

```bash
npm install
```

#### TypeScript and SVGs

To support SVG imports in TypeScript, a type definition for SVG modules is included. Ensure that the `custom.d.ts` file in the `src` directory contains the following:

```typescript
declare module "*.svg" {
  const content: any;
  export default content;
}
```

#### Starting the Development Server

To start the development server and view the project in a browser, run:

```bash
npm start
```

The application will be available at `http://localhost:3000`.

### Project Structure

- `src`: Contains all the TypeScript and React component files.
- `public`: Contains the static files like HTML and images.
- `tsconfig.json`: Configures TypeScript options.

---
