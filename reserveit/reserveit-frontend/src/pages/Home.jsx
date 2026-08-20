import Header from "../components/Header.jsx";
import Hero from "../components/Hero.jsx";
import ProofStrip from "../components/ProofStrip.jsx";
import Compare from "../components/Compare.jsx";
import Roles from "../components/Roles.jsx";
import Workflow from "../components/Workflow.jsx";
import Security from "../components/Security.jsx";
import Stack from "../components/Stack.jsx";
import FinalCta from "../components/FinalCta.jsx";
import Footer from "../components/Footer.jsx";

export default function Home() {
  return (
    <>
      <Header />
      <Hero />
      <ProofStrip />
      <Compare />
      <Roles />
      <Workflow />
      <Security />
      <Stack />
      <FinalCta />
      <Footer />
    </>
  );
}
