import { useReveal } from '../hooks/useReveal.js'

const STACK = [
  'Java + Spring Boot',
  'Spring Data JPA / Hibernate',
  'Spring Security',
  'PostgreSQL',
  'JWT authentication',
  'REST APIs',
  'Email notification service',
  'Maven',
  'React + Vite'
]

export default function Stack() {
  const headRef = useReveal()
  const listRef = useReveal()

  return (
    <section className="section section--tight" id="stack">
      <div className="wrap">
        <div className="section-head reveal" style={{ marginBottom: '1.8rem' }} ref={headRef}>
          <span className="eyebrow">Under the hood</span>
          <h2>Built on a straightforward, well-understood stack</h2>
        </div>
        <div className="stack-list reveal" ref={listRef}>
          {STACK.map((item) => (
            <span className="stack-chip" key={item}>{item}</span>
          ))}
        </div>
      </div>
    </section>
  )
}
