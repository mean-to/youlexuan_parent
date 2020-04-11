<!DOCTYPE html>
<html>

<head>
    </head>
<body>

<div>
    <#assign str='1'/>
    ${name}
    <#list list as nn>
        index:${nn_index}------${nn.name}
    </#list>
<br>
    <#if name??>
${name}
        <#else>
        ${str}
    </#if>
</div>
</body>
</html>