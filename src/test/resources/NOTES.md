<p>
All files in src/test/resources/pokemon meant to be targeted in tests for deserialization are expected to use PascalCase
and possess these segments left to right:
</p>
<ol>
    <li>Prefix Region Name</li>
    <li>
        Pokemon Name
        <ul>
            <li><p>
            This should only include alphanumeric characters, dahses, and underscores. Special characters in some Pokemon names like '♀' and ':'  are to be avoided to help
            keep file names uniform and prevent any issues.
            </p></li>
        </ul>
    </li>
    <li>Suffix of "Sample.json"</li>
</ol>